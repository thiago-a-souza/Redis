# Author
Thiago Alexandre Domingues de Souza

# Index

- [About](#about)
- [What is Redis?](#what-is-redis)
  - [@Cacheable](#cacheable)
  - [@CachePut](#cacheput)
  - [@CacheEvict](#cacheevict)
- [Requirements](#requirements)
- [Executing](#executing)
  - [Starting Containers](#starting-containers)
  - [Running the project](#running-the-project)
  - [Usage](#usage)

# About

This project implements an in-memory caching solution using Redis, Spring Boot, MongoDB, and Docker.

# What is Redis?

Redis, **RE**mote **DI**ctionary **S**erver, is a distributed in-memory key-value data store developed by Salvatore Sanfilippo in 2006 to improve the scalability of his application. Because data is stored in RAM, it provides a faster performance compared to traditional disk-based databases, making it appropriate for caching. Unlike memcached, which is another popular key-value data store, Redis supports advanced data structures such as *strings, lists, sets, sorted sets, hashes, bit arrays, and hyperloglogs*, making it more flexible to solve a wide range of problems. Also, it provides several features for high-scalable applications such as replication, sharding, transactions, disk persistence, etc.

Although Redis is frequently used for caching purposes, it can be used as a database, a message broker for pub/sub applications (e.g. chat rooms, real-time scores, etc.), geospatial applications, etc. When used as a cache, it evicts old data when the database reaches its limit, using a Least Recently Used approach, but it also provides a fine-grained control over eviction, allowing you to choose the most appropriate algorithm.


## @Cacheable

Annotation instructs Spring to verify if the key under the cache name provided is in cache, if no match is found it executes the method and store the result in cache. In other words, the method is executed only once for the cache name/key pair, after that it will retrieve the result from the in-memory cache.


**1. Caching example:** the *email* provided is stored as key for the *User* in the cache named *userCache*.

```
@Cacheable(cacheNames="userCache", key="#email")
public User findUserByEmail(String email) {
  return repository.findById(email).orElse(null);
}
```

**2. Redis-cli:** Listing cache values associated with the cache *userCache*

```
$ redis-cli

> keys userCache*
1) "userCache::test-3@email.com"
2) "userCache::test-2@email.com"
3) "userCache::test-1@email.com"
```

**3. Conditional caching:** not all circumstances require caching, the *conditional* parameter allows a SpEL expression that evaluates if the method should be cached or not. Remark: the condition parameter is evaluated **BEFORE** the method execution.

```
@Cacheable(cacheNames="userCache", key="#name", condition="#name.length < 10")
public User findUserByName(String name) {
  return repository.findById(email).orElse(null);
}
```

**4. Unless caching:** unlike the conditional parameter, unless expressions are evaluated **AFTER** the method execution. 

Example below will not store in cache if result is null.

```
@Cacheable(cacheNames="userCache", key="#email", unless = "#result == null")
public User findUserByEmail(String email) {
  return repository.findById(email).orElse(null);
}
```

Example below will not store in cache if result has more than 10 elements.

```
@Cacheable(cacheNames="usersAgeCache", key="#age", unless = "#result.size() > 10")
public List<User> findUserByAge(int age){
  return repository.findUsersByAge(age);
}
```



## @CachePut

Unlike methods that use the *@Cacheable* annotation, methods using the *@CachePut* annotation **always execute**, and the cache gets updated (unless conditional caching prevents that).

Method below updates the user's data and store the result in cache unless the method returns a null value.

```
@CachePut(value="userCache", key="#user.email", unless = "#result == null")
public User updateUser(User user) {
  return repository.save(user);
}
```



## @CacheEvict

Similar to *@CachePut*, *@CacheEvict* always execute, and it removes the corresponding key from cache.

Method below will remove the user with the given *email* from the repository and evict from cache.

```
@CacheEvict(value="userCache", key="#email")
public void deleteUser(String email) {
  repository.deleteById(email);
}
```

Method below removes all cache entries from the cache named *userCache*

```
@CacheEvict(value="userCache", allEntries=true)
public void clearUserCache() {		
}
```

# Requirements

- Java 8 or higher
- Maven
- Docker
- cURL

# Executing

## Starting containers

```
docker run -d -p 6379:6379 redis
docker run -d -p 27017:27017 mongo 
```

## Running the project

```
mvn spring-boot:run
```

## Usage

**1.** Adding a new user

```
curl -X POST -s http://localhost:8080/user \
-H "Content-Type: application/json" \
-d '{"name" : "test-1", "email" : "test-1@email.com", "age" : 20}'

curl -X POST -s http://localhost:8080/user \
-H "Content-Type: application/json" \
-d '{"name" : "test-2", "email" : "test-2@email.com", "age" : 30}'
```

**2.** Get user by email

```
curl -X GET -s http://localhost:8080/user/test-1@email.com 
```

**3.** Update user's data

```
curl -X PUT -s http://localhost:8080/user \
 -H "Content-Type: application/json" \
 -d '{"name" : "test-1 modified", "email" : "test-1@email.com", "age" : 30}'
```

**4.** Find users by age

```
curl -X GET -s http://localhost:8080/user/findByAge/30
```

**5.** Delete user by email

```
curl -X DELETE -s http://localhost:8080/user/deleteUser/test-1@email.com
```

**6.** Clearing cache 

```
curl -X DELETE -s http://localhost:8080/user/clearUserCache
```
