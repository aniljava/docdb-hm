docdb-hm
========
A key value backend for [docdb](https://github.com/aniljava/docdb) based on java HashMap.

About
=====

docdb-hm provides a in-memory key value. All updates are persisted on an
append only file. During the first use, all the key value mappings are read on
memory.

File Format
===========

File consists of series of entries. Entries can either be add, or delete.

[1 BYTE FOR META][1 BYTE LENGTH OF KEY][KEY][4 BYTE LENGTH OF VALUE][VALUE]

META : add = 0 last bit, delete 1 last bit.

The value part is not present if delete.

Example Use
=============

    DocDB db = new HMDB("somefile.db"); // Will be created if not exist.
    Map post = new HashMap();
    post.put("title","Example Post");
    // ...
    db.save("post", 1 , post); // id = 1
    
    // Getting.
    Map gpost = db.get("post", Map.class, 1);
    db.close();


Defragment and File Size
========================

When the file is opened the first time, if the number of entries in file is
larger than the twice file is optimized  by rewriting.
