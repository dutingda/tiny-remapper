package net.fabricmc.tinyremapper.util;

import net.fabricmc.tinyremapper.ClassInstance;
import net.fabricmc.tinyremapper.InputTag;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.ClassInstance;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import java.io.File;
import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class VersionAwareMapTest {

    @Test
    public void size() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        VersionedName v1 = new VersionedName("test", 1);
        VersionAwareMap m1 = new VersionAwareMap();
        assertEquals(m1.size(), 0);
        m1.put(v1, c1);
        assertEquals(m1.size(), 1);
        m1.remove(v1);
        assertEquals(m1.size(), 0);
        m1.put(v1, c1);
        m1.put(v1, c1);
        assertEquals(m1.size(), 1);
        VersionedName v2 = new VersionedName("test", 2);
        m1.put(v2, c1);
        assertEquals(m1.size(), 2);
    }

    @Test
    public void isEmpty() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        assertEquals(m1.isEmpty(), true);
        m1.remove(v1);
        assertEquals(m1.isEmpty(), true);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        assertEquals(m1.isEmpty(), false);
        m1.remove(v1);
        assertEquals(m1.isEmpty(), true);
    }

    @Test
    public void containsKey() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        assertNotEquals(v1, null);
        assertEquals(m1.containsKey(v1), true);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertEquals(m1.containsKey(v1), true);
        assertEquals(m1.containsKey(v2), true);
        m1.remove(v1);
        assertEquals(m1.containsKey(v2), true);
        assertEquals(m1.containsKey(v1), false);
    }

    @Test
    public void containsValue() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        assertEquals(m1.containsValue(c1), true);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertEquals(m1.containsValue(c1), true);
        assertEquals(m1.containsValue(c2), true);
        m1.remove(v1);
        assertEquals(m1.containsValue(c2), true);
        assertEquals(m1.containsValue(c1), false);
        m1.put(v2, c1);
        assertEquals(m1.containsValue(c2), false);
    }

    @Test
    public void get() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        assertNull(m1.get(v1));
        m1.put(v1, c1);
        assertSame(m1.get(v1), c1);
    }

    @Test
    public void getAllVersionClasses() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v2, c2);
        assertArrayEquals(m1.getAllVersionClasses("test").toArray(), new ClassInstance[]{c1, c2});
    }

    @Test
    public void getFeasibleVersionClasses() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v2, c2);
        assertArrayEquals(m1.getFeasibleVersionClasses(v2).toArray(), new ClassInstance[]{c1, c2});
        assertArrayEquals(m1.getFeasibleVersionClasses(v1).toArray(), new ClassInstance[]{c1});
        assertEquals(m1.getFeasibleVersionClasses(new VersionedName("tst", 3)).size(), 0);
        assertEquals(m1.getFeasibleVersionClasses(new VersionedName("test", VersionedName.EMPTY)).size(), 0);
        assertEquals(m1.getFeasibleVersionClasses(new VersionedName("test", 3)).size(), 2);
    }

    @Test
    public void testGetFeasibleVersionClasses() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    }

    @Test
    public void getByVersion() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v2, c2);
        assertEquals(m1.getByVersion(v2), c2);
        assertEquals(m1.getByVersion(v1), c1);
        assertNotNull(m1.getByVersion(new VersionedName("test", 3)));
        assertNull(m1.getByVersion(new VersionedName("tst", 3)));
        assertNull(m1.getByVersion(new VersionedName("test", VersionedName.EMPTY)));
    }

    @Test
    public void testGetByVersion() {
    }

    @Test
    public void put() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        assertEquals(m1.size(), 0);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertSame(m1.get(v1), c1);
        assertSame(m1.get(v2), c2);
        assertEquals(m1.size(), 2);
        m1.remove(v1);
        assertEquals(m1.size(), 1);
        m1.remove(v2);
        assertEquals(m1.isEmpty(), true);
    }

    @Test
    public void remove() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.remove(v1);
        assertEquals(m1.size(), 0);
        m1.put(v1, c1);
        assertEquals(m1.size(), 1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v1, c2);
        assertEquals(m1.size(), 1);
        m1.remove(v1);
        assertEquals(m1.size(), 0);
    }


    @Test
    public void putAll() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        VersionAwareMap m2 = new VersionAwareMap();
        m1.putAll(m2);
        assertEquals(m2.size(), 2);
        assertArrayEquals(m1.getAllVersionClasses("test").toArray(), m2.getAllVersionClasses("test").toArray());
    }

    @Test
    public void clear() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertEquals(m1.size(), 2);
        m1.clear();
        assertEquals(m1.isEmpty(), true);

    }

    @Test
    public void keySet() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertEquals(m1.keySet().size(), 2);
        Set<VersionedName> s = m1.keySet();
        assertArrayEquals(s.toArray(), new VersionedName[]{v1, v2});
        m1.remove(v2);
        assertEquals(s.size(), 1);
        assertArrayEquals(s.toArray(), new VersionedName[]{v1});

    }

    @Test
    public void values() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        VersionAwareMap m1 = new VersionAwareMap();
        TinyRemapper t1 = TinyRemapper.newRemapper().build();
        InputTag[] inputTags = {};
        VersionedName v1 = new VersionedName("test", 1);
        Constructor<ClassInstance> c = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        c.setAccessible(true);
        ClassInstance c1 = c.newInstance(t1, false, inputTags, null, null, 1);
        m1.put(v1, c1);
        VersionedName v2 = new VersionedName("test", 2);
        Constructor<ClassInstance> cc = ClassInstance.class.getDeclaredConstructor(TinyRemapper.class, boolean.class, InputTag[].class, Path.class, byte[].class, int.class);
        cc.setAccessible(true);
        ClassInstance c2 = cc.newInstance(t1, false, inputTags, null, null, 2);
        m1.put(v2, c2);
        assertEquals(m1.keySet().size(), 2);
        Collection<ClassInstance> s = m1.values();
        assertArrayEquals(s.toArray(), new ClassInstance[]{c1, c2});
        m1.remove(v2);
        assertEquals(s.size(), 1);
        assertArrayEquals(s.toArray(), new ClassInstance[]{c1});
    }

    @Test
    public void entrySet() {

    }

    @Test
    public void versions() {
    }

    @Test
    public void names() {
    }
}