# varint

## Clojure Kata: Varint

Inspired by this blog post: "Varint in Clojure"

http://garajeando.blogspot.com/2016/09/kata-varint-in-clojure-using-midje-and.html


Varint is a an encoding for numbers as used in Google [Protocol Buffers encoding][2].

[2]: https://developers.google.com/protocol-buffers/docs/encoding "Protocol Buffers Encoding"

Each byte in a varint, except the last byte, has the most significant bit set –
this indicates that there are further bytes to come. The lower 7 bits of each byte are
used to store the two's complement representation of the number in groups of 7 bits,
least significant group first.  The group terminates with a byte having the high bit
cleared.

I didn't like the blogged solution.  Too much string manipulation.  Representing bytes
as lists of strings "1" or "0" is wasteful.

For my Clojure implementation, I use a vector of longs (with each long value within the
byte range).  The "bytes" are basically unsigned, even though they're stored in signed
longs -- the standard Clojure integer type.

For serious work, it would make sense to use a Java byte-array.


## Copyright and License

Copyright (c) 2016 Stephen E. Miner.

Distributed under the Eclipse Public License, the same as Clojure.
