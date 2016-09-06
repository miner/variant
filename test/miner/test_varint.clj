(ns miner.test-varint
  (:require [clojure.test :refer :all]
            [miner.varint :refer :all]))


(deftest basics
  (is (= (encode 1) "00000001"))
  (is (= (encode 8) "00001000"))
  (is (= (encode 127) "01111111"))
  (is (= (encode 300) "10101100 00000010"))
  (is (= (decode "10101100 00000010") 300)))

(deftest extremes
  (are [x] (= x (varint->long (varint x)))
    Long/MIN_VALUE
    Long/MAX_VALUE
    Integer/MIN_VALUE
    Integer/MAX_VALUE
    0
    1
    -1))

(deftest exhaustive
  (dotimes [n 1e4]
    (is (= n (varint->long (varint n))))
    (is (= (- n) (varint->long (varint (- n)))))
    (is (= n (decode (encode n))))
    (is (= (- n) (decode (encode (- n)))))))


