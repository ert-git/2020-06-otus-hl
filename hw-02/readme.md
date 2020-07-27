
# Без индексов 

## План выполнения 

```json
mysql> EXPLAIN FORMAT=JSON SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;

{
  "query_block": {
    "select_id": 1,
    "cost_info": {
      "query_cost": "418520.00"
    },
    "ordering_operation": {
      "using_filesort": false,
      "table": {
        "table_name": "users",
        "access_type": "index",
        "key": "PRIMARY",
        "used_key_parts": [
          "id"
        ],
        "key_length": "4",
        "rows_examined_per_scan": 1981940,
        "rows_produced_per_join": 24463,
        "filtered": "1.23",
        "cost_info": {
          "read_cost": "413627.30",
          "eval_cost": "4892.70",
          "prefix_cost": "418520.00",
          "data_read_per_join": "46M"
        },
        "used_columns": [
          "id",
          "first_name",
          "last_name",
          "second_name",
          "age",
          "gender",
          "city",
          "login",
          "password"
        ],
        "attached_condition": "((`otus`.`users`.`first_name` like 'Jon%') and (`otus`.`users`.`last_name` like 'Dog%'))"
      }
    }
  }
} 
```

```
mysql> EXPLAIN extended SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;
+----+-------------+-------+------------+-------+---------------+---------+---------+------+---------+----------+-------------+
| id | select_type | table | partitions | type  | possible_keys | key     | key_len | ref  | rows    | filtered | Extra       |
+----+-------------+-------+------------+-------+---------------+---------+---------+------+---------+----------+-------------+
|  1 | SIMPLE      | users | NULL       | index | NULL          | PRIMARY | 4       | NULL | 1981940 |     1.23 | Using where |
+----+-------------+-------+------------+-------+---------------+---------+---------+------+---------+----------+-------------+
```

```
mysql> SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;
Empty set (8.79 sec)
```

## Результаты тестов

### 1 соединение

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t1 -c1 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  1 threads and 1 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   779.19ms    8.48ms 845.89ms   74.55%
    Req/Sec     1.00      0.00     1.00    100.00%
  Latency Distribution
     50%  777.79ms
     75%  783.54ms
     90%  790.19ms
     99%  806.62ms
  385 requests in 5.00m, 523.42KB read
Requests/sec:      1.28
Transfer/sec:      1.74KB

```

### 10 соединений

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t10 -c10 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  10 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.46s    74.38ms   3.96s    78.26%
    Req/Sec     0.00      0.00     0.00    100.00%
  Latency Distribution
     50%    3.45s
     75%    3.49s
     90%    3.54s
     99%    3.72s
  860 requests in 5.00m, 1.14MB read
Requests/sec:      2.87
Transfer/sec:      3.90KB
```

### 100 соединений

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t100 -c100 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  100 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.08m    49.41s    2.87m    80.00%
    Req/Sec     0.00      0.00     0.00    100.00%
  Latency Distribution
     50%   34.47s
     75%    1.61m
     90%    2.85m
     99%    2.87m
  450 requests in 5.00m, 611.72KB read
Requests/sec:      1.50
Transfer/sec:      2.04KB

```

### 1000 соединений

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t100 -c1000 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  100 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.70m    41.72s    2.57m    33.04%
    Req/Sec     7.86      8.10    50.00     86.09%
  Latency Distribution
     50%    1.70m
     75%    2.55m
     90%    2.55m
     99%    2.57m
  750 requests in 5.00m, 1.00MB read
  Socket errors: connect 0, read 0, write 193, timeout 299
Requests/sec:      2.50
Transfer/sec:      3.40KB
```


## Графики

[График latency от числа соединений] (https://yadi.sk/i/WPdQXMAGoVvgkQ)
[График request/сек от числа соединений] (https://yadi.sk/i/PYK2GJVMdF7cNA)


# С индексами

```sql
 CREATE INDEX idx_fn on users (first_name);
 CREATE INDEX idx_ln on users (last_name);
```

## План выполнения 

```json
mysql> EXPLAIN FORMAT=JSON SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;

 {
  "query_block": {
    "select_id": 1,
    "cost_info": {
      "query_cost": "2.52"
    },
    "ordering_operation": {
      "using_filesort": true,
      "cost_info": {
        "sort_cost": "0.11"
      },
      "table": {
        "table_name": "users",
        "access_type": "range",
        "possible_keys": [
          "idx_fn"
        ],
        "key": "idx_fn",
        "used_key_parts": [
          "first_name"
        ],
        "key_length": "306",
        "rows_examined_per_scan": 1,
        "rows_produced_per_join": 0,
        "filtered": "11.11",
        "index_condition": "((`otus`.`users`.`first_name` like 'Jon%') and (`otus`.`users`.`last_name` like 'Dog%'))",
        "cost_info": {
          "read_cost": "2.39",
          "eval_cost": "0.02",
          "prefix_cost": "2.41",
          "data_read_per_join": "223"
        },
        "used_columns": [
          "id",
          "first_name",
          "last_name",
          "second_name",
          "age",
          "gender",
          "city",
          "login",
          "password"
        ]
      }
    }
  }
} 
```

```
mysql> mysql> EXPLAIN extended SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;
+----+-------------+-------+------------+-------+---------------+--------+---------+------+------+----------+---------------------------------------+
| id | select_type | table | partitions | type  | possible_keys | key    | key_len | ref  | rows | filtered | Extra                                 |
+----+-------------+-------+------------+-------+---------------+--------+---------+------+------+----------+---------------------------------------+
|  1 | SIMPLE      | users | NULL       | range | idx_fn        | idx_fn | 306     | NULL |    1 |    11.11 | Using index condition; Using filesort |
+----+-------------+-------+------------+-------+---------------+--------+---------+------+------+----------+---------------------------------------+
```

```
mysql> SELECT * from users WHERE first_name LIKE 'Jon%' AND last_name LIKE 'Dog%' order by id;
Empty set (0.01 sec)
```

## Результаты тестов

### 1 соединение

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t1 -c1 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  1 threads and 1 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     7.47ms    7.41ms 143.17ms   95.74%
    Req/Sec   154.05     40.58   232.00     70.16%
  Latency Distribution
     50%    5.85ms
     75%    7.99ms
     90%    8.10ms
     99%   43.67ms
  45786 requests in 5.00m, 60.79MB read
Requests/sec:    152.60
Transfer/sec:    207.47KB
```

### 10 соединений
```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t10 -c10 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  10 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    22.62ms   16.19ms 265.18ms   93.84%
    Req/Sec    47.73     11.98    90.00     61.13%
  Latency Distribution
     50%   19.41ms
     75%   25.40ms
     90%   33.27ms
     99%   90.91ms
  142306 requests in 5.00m, 188.94MB read
Requests/sec:    474.21
Transfer/sec:    644.71KB

```

### 100 соединений

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t100 -c100 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  100 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   218.91ms  175.81ms   3.36s    90.62%
    Req/Sec     6.03      3.54    70.00     61.46%
  Latency Distribution
     50%  195.38ms
     75%  265.17ms
     90%  351.09ms
     99%  692.46ms
  146275 requests in 5.00m, 194.21MB read
Requests/sec:    487.43
Transfer/sec:    662.69KB

```

### 1000 соединений

```
u1@u-1:~$ /usr/local/bin/wrk/wrk -t1000 -c1000 -d300s --timeout 200s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  1000 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     2.19s   757.71ms  11.57s    90.45%
    Req/Sec     0.10      0.63    10.00     96.27%
  Latency Distribution
     50%    2.09s
     75%    2.35s
     90%    2.69s
     99%    5.23s
  136776 requests in 5.00m, 181.59MB read
  Socket errors: connect 0, read 0, write 161, timeout 0
Requests/sec:    455.70
Transfer/sec:    619.53KB

```


## Графики

[График latency от числа соединений] (https://yadi.sk/i/mItAKqdF97Hlrw)
[График request/сек от числа соединений] (https://yadi.sk/i/EWlMY1rwrj-NFQ)

# Выводы

Создание индекса дает ускорение времени выполнения запроса примерно в 500 раз. Хотя с ростом кол-ва соединений максимальное время отклика все равно становится ощутимым. Было обнаружено два любопытных факта.
1. EXPLAIN всегда дает `Using filesort` при сортировке по столбцу, не являющегося первым в используемом индексе.
2. Время выборки не уменьшается при добавлении в индекс второго столбца выборки. Вероятно, объясняется это тем, что для фильтрацию по второму столбцу индекс уже не нужен, действие производится над выборкой, полученной по первому столбцу. Учитывая этот факт, имеет смысл создать два индекса (отдельно на `first_name` и `last_name`), чтобы получать преимущество индекса не только при LIKE по обоим полям одновремено, но и по отдельности (т.е. для запросов вида `where last_name like ?`).



