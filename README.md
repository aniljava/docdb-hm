docdb-hm
========

Java Hashmap backed DocDB implementation; In-memory Document Database

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
