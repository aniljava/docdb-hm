package docdb.hm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import docdb.DocDB;
import docdb.KV;

public class HMDB extends DocDB implements KV {

    public HMDB(String fileName) {
        this.fileName = fileName;
        boolean optimize = false;

        if (fileName.indexOf('?') != -1) {
            if (fileName.indexOf("optimize") != -1) optimize = true;
            this.fileName = fileName.substring(0, fileName.indexOf('?'));
        }

        try {
            client = new HashMap<String, String>();

            if (new File(this.fileName).exists()) {
                DataInputStream in = new DataInputStream(new FileInputStream(this.fileName));
                int i;
                while ((i = in.read()) != -1) {
                    int rw = i & 1;
                    int keysize = in.read();
                    byte key[] = new byte[keysize];
                    in.readFully(key);

                    if (rw == 0) { // ADD
                        byte value[] = null;
                        int length = in.readInt();

                        value = new byte[length];
                        in.readFully(value);
                        client.put(str(key), str(value));

                    } else {
                        client.remove(key);
                    }
                }
                in.close();
            }

            if (optimize) optimize();

            out = new DataOutputStream(new FileOutputStream(this.fileName, true));
            super.setKV(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Map<String, String> client;
    private DataOutputStream   out;
    private String             fileName;

    public void optimize() throws Exception {

        out = new DataOutputStream(new FileOutputStream(fileName, false));
        for (String key : client.keySet()) {

            byte bkey[] = key.getBytes();
            byte value[] = client.get(key).getBytes();

            out.writeByte(0); // add
            out.writeByte(bkey.length);
            out.write(bkey);
            out.writeInt(value.length);
            out.write(value);

        }
        out.flush();
        out.close();
    }

    public byte[] get(byte[] key) {
        String str = client.get(str(key));
        if (str == null) return null;
        return str.getBytes();
    }

    public void set(byte[] key, byte[] value) {
        if (value == null) return;
        if (str(value).equals(str(get(key)))) return; // Nothing to do.

        client.put(str(key), str(value));
        try {
            out.writeByte(0);
            out.writeByte(key.length);
            out.write(key);
            out.writeInt(value.length);
            out.write(value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void remove(byte[] key) {
        byte[] str = get(key);
        if (str == null) return;

        client.remove(str(key));

        try {
            out.writeByte(1);
            out.writeByte(key.length);
            out.write(key);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private String str(byte data[]) {
        if (data == null) return null;
        return new String(data);
    }

    public Object getKV() {
        return client;
    }

    public void close() {
        try {
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void clear() {
        try {
            out.close();
            client.clear();
            out = new DataOutputStream(new FileOutputStream(fileName, false));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
