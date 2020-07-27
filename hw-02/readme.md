
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
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t1 -c1 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  1 threads and 1 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   806.17ms   26.68ms   1.20s    97.58%
    Req/Sec     0.99      0.07     1.00     99.46%
  Latency Distribution
     50%  802.40ms
     75%  809.69ms
     90%  817.36ms
     99%  953.87ms
  372 requests in 5.00m, 505.74KB read
Requests/sec:      1.24
Transfer/sec:      1.69KB
```

### 10 соединений

```
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t10 -c10 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  10 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     6.78s    69.28ms   7.17s    84.77%
    Req/Sec     0.00      0.00     0.00    100.00%
  Latency Distribution
     50%    6.76s
     75%    6.79s
     90%    6.84s
     99%    7.08s
  440 requests in 5.00m, 598.12KB read
Requests/sec:      1.47
Transfer/sec:      1.99KB
```

### 100 соединений

```
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t100 -c100 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  100 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.13m   540.17ms   1.19m    78.25%
    Req/Sec     0.00      0.00     0.00    100.00%
  Latency Distribution
     50%    1.12m
     75%    1.13m
     90%    1.14m
     99%    1.16m
  400 requests in 5.00m, 543.75KB read
Requests/sec:      1.33
Transfer/sec:      1.81KB
```

## Графики
[График latency от числа соединений]: https://yadi.sk/i/7iZP6JrhEBeWNw 
[График request/сек от числа соединений]: https://yadi.sk/i/4w0X39LK6iw1uQ


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
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t1 -c1 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output-1-after.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  1 threads and 1 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     5.50ms    7.02ms 168.05ms   97.64%
    Req/Sec   210.06     30.00   250.00     87.43%
  Latency Distribution
     50%    4.50ms
     75%    4.87ms
     90%    5.41ms
     99%   33.65ms
  62650 requests in 5.00m, 83.18MB read
Requests/sec:    208.80
Transfer/sec:    283.88KB
```

### 10 соединений
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t10 -c10 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output-10-after.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  10 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.48ms   26.52ms 694.02ms   94.69%
    Req/Sec    29.47      7.66   101.00     57.29%
  Latency Distribution
     50%   31.90ms
     75%   38.07ms
     90%   48.92ms
     99%  132.15ms
  87538 requests in 5.00m, 116.22MB read
Requests/sec:    291.70
Transfer/sec:    396.58KB
```

### 100 соединений

```
u1@u-1:~/otus$ /usr/local/bin/wrk/wrk -t10 -c100 -d300s --timeout 100s --latency -s /home/u1/otus/post.lua http://192.168.11.165:8080/otus/search-users.html |  wrk2img ./reports/output-100-after.png
Running 5m test @ http://192.168.11.165:8080/otus/search-users.html
  10 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   363.35ms  205.83ms   5.57s    95.33%
    Req/Sec    29.57     15.46   101.00     65.13%
  Latency Distribution
     50%  342.96ms
     75%  393.52ms
     90%  460.17ms
     99%    1.06s
  85878 requests in 5.00m, 114.02MB read
Requests/sec:    286.19
Transfer/sec:    389.09KB
```

## Графики
[График latency от числа соединений]: https://yadi.sk/i/FQL0QAGFyaACUA 
[График request/сек от числа соединений]: https://yadi.sk/i/96qIt6u_i-P5ow 

# Выводы

Создание индекса дает ускорение времени выполнения запроса примерно в 500 раз. Хотя с ростом кол-ва соединений максимальное время отклика все равно становится ощутимым. Было обнаружено два любопытных факта.
1. EXPLAIN всегда дает `Using filesort` при сортировке по столбцу, не являющегося первым в используемом индексе.
2. Время выборки не уменьшается при добавлении в индекс второго столбца выборки. Вероятно, объясняется это тем, что для фильтрацию по второму столбцу индекс уже не нужен, действие производится над выборкой, полученной по первому столбцу. Учитывая этот факт, имеет смысл создать два индекса (отдельно на `first_name` и `last_name`), чтобы получать преимущество индекса не только при LIKE по обоим полям одновремено, но и по отдельности (т.е. для запросов вида `where last_name like ?`).



