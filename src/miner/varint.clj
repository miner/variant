(ns miner.varint
  (:require [clojure.string :as str]))


;; varint kata blog post:
;; http://garajeando.blogspot.com/2016/09/kata-varint-in-clojure-using-midje-and.html
;;
;; his solution:
;; https://github.com/trikitrok/varint-kata-clojure
;; 
;; I didn't like the blogged solution.  Too much string manipulation.  Representing bytes
;; as lists of strings "1" or "0" is wasteful.

;; Each byte in a varint, except the last byte, has the most significant bit (msb) set –
;; this indicates that there are further bytes to come. The lower 7 bits of each byte are
;; used to store the two's complement representation of the number in groups of 7 bits,
;; least significant group first.

;; NOTE: varints aren't so good for negative numbers.  From the Google Protocol Buffers doc:
;; https://developers.google.com/protocol-buffers/docs/encoding#structure
;;
;;   If you use int32 or int64 as the type for a negative number, the resulting varint is
;;   always ten bytes long – it is, effectively, treated like a very large unsigned integer.
;;
;; Google protocol buffers actually use a "zig-zag" encoding for negatives to avoid this
;; problem.   That is, convert n to (n << 1) ^ (n >> 63) and then varint.  They have a field
;; type encoded with the value.



;; For my Clojure implementation, I use a vector of longs (with each long value within the
;; byte range).  The "bytes" are basically unsigned, even though they're stored in signed
;; longs as the standard Clojure integer type.
;;
;; For serious work, it would make sense to use a Java byte-array.


;; base 128 varint
(defn varint
  "Returns vector of longs (within byte range, 0-255) according to varint encoding.
   LSB comes first, all bytes except final (MSB) have high bit set indicating more to follow."
  [n]
  (loop [vi []  r n]
    (if (= (bit-and (bit-not 0x7F) r) 0)
      (conj vi r)
      (recur (conj vi (bit-or 0x80 (bit-and 0x7F r)))
             (unsigned-bit-shift-right r 7)))))

;; bit-or is slightly faster than bit-set
;; better to use bit-and+bit-not than to do (<= 0 x 127)

(defn varint->long [varint]
  (reduce (fn [n shift] (bit-or n (bit-shift-left (bit-and 0x7F (varint shift)) (* shift 7))))
          0
          (range (count varint))))


;; String encoding and decoding are not so efficient, but useful for testing.
;; My string encoding adds a space between 8-bit sections for readability.

(defn byte-str [b]
  ;; convert long "byte" to binary str, left-padded with 0s
  (let [bstr (Long/toBinaryString b)
        len (.length ^String bstr)]
    (str (subs "00000000" len) bstr)))


(defn encode [n]
  (str/join " " (map byte-str (varint n))))

;; check "byte" length = 8 for sanity, avoids silent typos
(defn parse-binary-byte [s]
  (assert (= (.length ^String s) 8))
  (Long/valueOf ^String s 2))

(defn decode [varint-string]
  (varint->long (mapv parse-binary-byte (str/split varint-string #" "))))
