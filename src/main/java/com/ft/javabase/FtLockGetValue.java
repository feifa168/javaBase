package com.ft.javabase;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FtLockGetValue {
    private HashMap<String, Object> map = new HashMap<>();
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private Lock rLock = rwLock.readLock();
    private Lock wLock = rwLock.writeLock();

    // 不支持多线程
    public Object getValue(String key) {
        Object value = map.get(key);
        if (null == value) {
            value = "this is read from db";
            map.put(key, value);
        }
        return value;
    }

    // 多线程会有较多碰撞
    public synchronized Object getValueSyn(String key) {
        Object value = map.get(key);
        if (null == value) {
            value = "this is read from db";
            map.put(key, value);
        }
        return value;
    }

    // 多线程碰撞几率小于getValueSyn
    public Object getValueLocalSyn(String key) {
        Object value = map.get(key);
        if (null == value) {
            synchronized (this) {
                if (null == value) {
                    value = "this is read from db";
                    map.put(key, value);
                }
            }
        }
        return value;
    }

    // 类似于getValueLocalSyn，加锁解锁较为麻烦
    public Object getValueRWLock(String key) {
        Object value = null;
        try {
            rLock.lock();
            value = map.get(key);
            if (null == value) {
                try {
                    rLock.unlock();
                    wLock.lock();
                    if (null == value) {
                        value = "this is read from db";
                        map.put(key, value);
                    }
                } finally {
                    wLock.unlock();
                    rLock.lock();
                }
            }
        } finally {
            rLock.unlock();
        }

        return value;
    }
}
