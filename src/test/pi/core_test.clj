(ns pi.core-test
  (:require [clojure.test :refer :all]
            [pi.core :refer :all]))


(deftest str-to-char-count-test
  (testing "Testing str-to-char-count"
    (is (= (str-to-char-count "aabccc") '([\a 2] [\c 3])))))

(deftest tag-test
  (testing "Testing tag"
    (is (= (tag 1 [[\a 2] [\b 1]]) '([1 \a 2] [1 \b 1])))))

(deftest str-to-char-count-and-tag-test
  (testing "str-to-char-count-and-tag"
    (is (= (str-to-char-count-and-tag 1 "aabccc") '([1 \a 2] [1 \c 3])))
    (is (= (str-to-char-count-and-tag 2 "aabccc") '([2 \a 2] [2 \c 3])))
    ))

(deftest resolution-return-equal-test
  (testing "resolution-return-equal"
    (is (= (resolution-return-equal '([1 \a 2] [2 \a 2])) '(["=" \a 2] ["=" \a 2]) ))
    ))

(deftest resolution-return-list-of-ids-test
  (testing "resolution-return-list-of-ids"
    (is (= (resolution-return-list-of-ids '([1 \a 2] [2 \a 2] [3 \a 2])) '(["1,2,3" \a 2] ["1,2,3" \a 2] ["1,2,3" \a 2]) ))
    ))


(deftest mix-test-two-args-simple
  (testing "Testing mix [simple texts]"
    (let [s1 "abcccdd"
          s2 "aabdd"
          ]
      (is (= (mix s1 s2) "1:ccc/2:aa/=:dd")))
    (let [s1 "aabcccd"
          s2 "aabdd"
          ]
      (is (= (mix s1 s2) "1:ccc/2:dd/=:aa")))
    ))

(deftest mix-test-two-args
  (testing "Testing mix [complex texts]"
    (let [s1 "my&friend&Paul has heavy hats! &"
          s2 "my friend John has many many friends &"
          ]
      (is (= (mix s1 s2) "2:nnnnn/1:aaaa/1:hhh/2:mmm/2:yyy/2:dd/2:ff/2:ii/2:rr/=:ee/=:ss")))
    (let [s1 "mmmmm m nnnnn y&friend&Paul has heavy hats! &"
          s2 "my frie n d Joh n has ma n y ma n y frie n ds n&"
          ]
      (is (= (mix s1 s2) "1:mmmmmm/=:nnnnnn/1:aaaa/1:hhh/2:yyy/2:dd/2:ff/2:ii/2:rr/=:ee/=:ss")))
    (let [s1 "Are the kids at home? aaaaa fffff"
          s2 "Yes they are here! aaaaa fffff"
          ]
      (is (= (mix s1 s2) "=:aaaaaa/2:eeeee/=:fffff/1:tt/2:rr/=:hh")))
    ))

(deftest mix-test-multi-args-simple
  (testing "Testing mix [simple texts]"
    (let [s1 "xxx"
          ]
      (is (= (mix s1 s1 s1) "1,2,3:xxx")))
    (let [s1 "abcccdd"
          s2 "aabdd"
          s3 "aabbdd"
          s4 "aaa"
          ]
      (is (= (mix s1 s2 s3 s4) "1:ccc/4:aaa/1,2,3:dd/3:bb")))
    ))