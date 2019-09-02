package test.dependency;

import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.NodeDependency;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class SimpleDependencyTests extends DependencyTestBase {

    @Test
    //TODO rewrite
    public void simpleDepTest() {
        String src = "package at.aau.difftest;\n" +
                "\n" +
                "public class MyApplication {\n" +
                "\t\n" +
                "    public String abbrevate(String src, Integer maxLength) {\n" +
                "        // do some stuff\n" +
                "        return src;\n" +
                "    }\n" +
                "}";
        String dst = "package at.aau.difftest;\n" +
                "\n" +
                "//useless imports\n" +
                "import fake.io.PrintWriter;\n" +
                "import fake.util.Random;\n" +
                "\n" +
                "import org.apache.commons.lang3.StringUtils;\n" +
                "\n" +
                "\n" +
                "public class MyApplication {\n" +
                "\t\n" +
                "    public String abbrevate(String src, Integer maxLength) {\n" +
                "        // do some stuff\n" +
                "        return StringUtils.abbreviate(src, maxLength);\n" +
                "    }\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);

        // StringUtils usage:
        // TYPE usage in static class access
        // CALL on  StringUtils.abbreviate(...)
        //Assert.assertTrue(containsClassDependency(dependencyChanges, "org.apache.commons.lang3.StringUtils", Type.ADDED));
        // Unused dependency will not be shown
        //Assert.assertFalse(containsClassDependency(dependencyChanges, "fake.io.PrintWriter", Type.ADDED));

    }

    @Test
    //TODO rewrite
    public void insertedVSAdded() {
        String src = "package at.aau.difftest;\n" +
                "\n" +
                "import org.apache.commons.lang3.StringUtils;\n" +
                "public class MyApplication {\n" +
                "\t\n" +
                "    public String abbrevate(String src, Integer maxLength) {\n" +
                "        // do some stuff\n" +
                "        return StringUtils.abbreviate(src, maxLength);\n" +
                "    }\n" +
                "}";
        String dst = "package at.aau.difftest;\n" +
                "\n" +
                "//useless imports\n" +
                "import fake.io.PrintWriter;\n" +
                "import fake.util.Random;\n" +
                "\n" +
                "import org.apache.commons.lang3.StringUtils2;\n" +
                "\n" +
                "\n" +
                "public class MyApplication {\n" +
                "\t\n" +
                "    public String abbrevate(String src, Integer maxLength) {\n" +
                "        StringUtils2.insertedButNotAddedDependency();\n" +
                "        return StringUtils.abbreviate(src, maxLength);\n" +
                "    }\n" +
                "}";

//        DependencyChanges fileDependencyChanges = classifyDependencies(src, dst, true);
//        System.out.println(fileDependencyChanges.getChangedDependenciesAsString());

//        System.out.println(dumpStringChanges(src,dst));


        // StringUtils usage:
        // TYPE usage in static class access
        // CALL on  StringUtils.abbreviate(...)
//        Assert.assertTrue(countClassDependency(fileDependencyChanges, "org.apache.commons.lang3.StringUtils", Type.ADDED) == 2);
//        // Unused dependency will not be shown
//        Assert.assertTrue(countClassDependency(fileDependencyChanges, "fake.io.PrintWriter", Type.ADDED) == 0);

    }


    @Test
    //TODO rewrite
    public void newDependencyTest() {
        String src = "package com.vogella.tasks.ui.parts;\n" +
                "\n" +
                "import java.util.logging.Logger;\n" +
                "\n" +
                "public class MyClass {\n" +
                "\n" +
                "    private Logger logger;\n" +
                "\n" +
                "    public void someMethod(Logger logger) {\n" +
                "        this.logger = logger;\n" +
                "        // write an info log message\n" +
                "        logger.info(\"This is a log message.\")\n" +
                "    }\n" +
                "}";
        String dst = "package com.vogella.tasks.ui.parts;\n" +
                "\n" +
                "import java.util.logging.Logger;\n" +
                "\n" +
                "public class MyClass {\n" +
                "\n" +
                "    private Logger logger;\n" +
                "    private Muller muller;\n" +
                "\n" +
                "    public void someMethod(Logger logger) {\n" +
                "        this.logger = logger;\n" +
                "        this.muller = new Muller();  \n" +
                "    }\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);
        // StringUtils usage:
        // TYPE usage in static class access
        // CALL on  StringUtils.abbreviate(...)
        //Assert.assertTrue(containsClassDependency(dependencyChanges, "com.vogella.tasks.ui.parts.Muller", Type.ADDED));
//        // Unused dependency will not be shown
//        Assert.assertTrue(countClassDependency(dependencyChanges, "fake.io.PrintWriter", Type.ADDED) == 0);

    }


    @Test
    //TODO rewrite
    public void worldJavaQuickGDXUndefiendTest() {
        String src = "package eu.quickgdx.game.mechanics;\n" +
                "\n" +
                "import com.badlogic.gdx.graphics.Texture;\n" +
                "import com.badlogic.gdx.graphics.g2d.SpriteBatch;\n" +
                "import com.badlogic.gdx.graphics.glutils.ShapeRenderer;\n" +
                "import com.badlogic.gdx.maps.MapObjects;\n" +
                "import com.badlogic.gdx.maps.MapProperties;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMap;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMapRenderer;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;\n" +
                "import com.badlogic.gdx.maps.tiled.TmxMapLoader;\n" +
                "import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;\n" +
                "import com.badlogic.gdx.math.Vector2;\n" +
                "import com.badlogic.gdx.math.Vector3;\n" +
                "import com.badlogic.gdx.utils.Array;\n" +
                "import eu.quickgdx.game.mechanics.entities.CollisionObject;\n" +
                "import eu.quickgdx.game.mechanics.entities.ControlledObject;\n" +
                "import eu.quickgdx.game.mechanics.entities.GameObject;\n" +
                "import eu.quickgdx.game.mechanics.hud.HUD;\n" +
                "import eu.quickgdx.game.screens.GameplayScreen;\n" +
                "\n" +
                "/**\n" +
                " * Created by Veit on 06.02.2016.\n" +
                " */\n" +
                "public class World {\n" +
                "    public static final float SCALE = 2.5f;\n" +
                "    public Array<GameObject> gameObjects;\n" +
                "    public GameplayScreen gameplayScreen;\n" +
                "    public HUD hud;\n" +
                "    ShapeRenderer sr = new ShapeRenderer();\n" +
                "    ControlledObject controlledObject;\n" +
                "\n" +
                "    //Tiled Map Variables\n" +
                "    String level = \"level/sampleMap.tmx\"; //This is your example Tiled Map.\n" +
                "    TiledMap map;\n" +
                "    TiledMapRenderer tiledMapRenderer;\n" +
                "    int mapWidth;\n" +
                "    int tileWidth;\n" +
                "    int mapHeight;\n" +
                "    int tileHeight;\n" +
                "\n" +
                "    public World(GameplayScreen gameplayScreen) {\n" +
                "        gameObjects = new Array<GameObject>();\n" +
                "        this.gameplayScreen = gameplayScreen;\n" +
                "        loadTiledMap();\n" +
                "        //Add HUD\n" +
                "        this.hud = new HUD(controlledObject, this);\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "}";
        String dst = "package eu.quickgdx.game.mechanics;\n" +
                "\n" +
                "import com.badlogic.gdx.graphics.Texture;\n" +
                "import com.badlogic.gdx.graphics.g2d.SpriteBatch;\n" +
                "import com.badlogic.gdx.graphics.glutils.ShapeRenderer;\n" +
                "import com.badlogic.gdx.maps.MapObjects;\n" +
                "import com.badlogic.gdx.maps.MapProperties;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMap;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMapRenderer;\n" +
                "import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;\n" +
                "import com.badlogic.gdx.maps.tiled.TmxMapLoader;\n" +
                "import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;\n" +
                "import com.badlogic.gdx.math.Vector2;\n" +
                "import com.badlogic.gdx.math.Vector3;\n" +
                "import com.badlogic.gdx.utils.Array;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "import eu.quickgdx.game.mechanics.entities.CollisionObject;\n" +
                "import eu.quickgdx.game.mechanics.entities.ControlledObject;\n" +
                "import eu.quickgdx.game.mechanics.entities.Entity;\n" +
                "import eu.quickgdx.game.mechanics.entities.GameObject;\n" +
                "import eu.quickgdx.game.mechanics.entities.components.EntityComponent;\n" +
                "import eu.quickgdx.game.mechanics.entities.components.PositionComponent;\n" +
                "import eu.quickgdx.game.mechanics.entities.components.TextureComponent;\n" +
                "import eu.quickgdx.game.mechanics.hud.HUD;\n" +
                "import eu.quickgdx.game.screens.GameplayScreen;\n" +
                "\n" +
                "/**\n" +
                " * Created by Veit on 06.02.2016.\n" +
                " */\n" +
                "public class World {\n" +
                "    public static final float SCALE = 2.5f;\n" +
                "    public Array<GameObject> gameObjects;\n" +
                "    public GameplayScreen gameplayScreen;\n" +
                "    public HUD hud;\n" +
                "    ShapeRenderer sr = new ShapeRenderer();\n" +
                "    ControlledObject controlledObject;\n" +
                "\n" +
                "    //Tiled Map Variables\n" +
                "    String level = \"level/sampleMap.tmx\"; //This is your example Tiled Map.\n" +
                "    TiledMap map;\n" +
                "    TiledMapRenderer tiledMapRenderer;\n" +
                "    int mapWidth;\n" +
                "    int tileWidth;\n" +
                "    int mapHeight;\n" +
                "    int tileHeight;\n" +
                "\n" +
                "    /**\n" +
                "     * Component system based fields\n" +
                "     */\n" +
                "    public HashMap<Class, Array<Entity>> componentEntityHashMap = new HashMap<Class, Array<Entity>>();\n" +
                "\n" +
                "\n" +
                "\n" +
                "    public World(GameplayScreen gameplayScreen) {\n" +
                "        gameObjects = new Array<GameObject>();\n" +
                "        this.gameplayScreen = gameplayScreen;\n" +
                "        loadTiledMap();\n" +
                "        //Add HUD\n" +
                "        this.hud = new HUD(controlledObject, this);\n" +
                "\n" +
                "\n" +
                "        //Test Entities:\n" +
                "        Entity entity1 = new Entity(this);\n" +
                "        entity1.addComponent(new PositionComponent(new Vector2(100, 100)));\n" +
                "        entity1.addComponent(new TextureComponent((Texture)gameplayScreen.parentGame.getAssetManager().get(\"hud/life_small.png\")));\n" +
                "\n" +
                "        Entity entity2 = new Entity(this);\n" +
                "        entity2.addComponent(new PositionComponent(new Vector2(200,200)));\n" +
                "        entity2.addComponent(new TextureComponent((Texture)gameplayScreen.parentGame.getAssetManager().get(\"hud/life_small.png\")));\n" +
                "\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);
        //System.out.println(dependencyChanges.getDependencyChangeOverview());
        // StringUtils usage:
        // TYPE usage in static class access
        // CALL on  StringUtils.abbreviate(...)
        //Assert.assertTrue(containsClassDependency(dependencyChanges, "com.vogella.tasks.ui.parts.Muller", Type.ADDED));
//        // Unused dependency will not be shown
//        Assert.assertTrue(countClassDependency(dependencyChanges, "fake.io.PrintWriter", Type.ADDED) == 0);

    }



    @Test
    //TODO rewrite
    public void oneDependencyAddedCompletelyNew() {
        String src = "package eu.quickgdx.game.mechanics;\n" +
                "public class World {\n" +
                "    public GameplayScreen gameplayScreen;\n" +
                "\n" +
                "    public World(GameplayScreen gameplayScreen) {\n" +
                "        this.gameplayScreen = gameplayScreen;\n" +
                "    }\n" +
                "}";
        String dst = "package eu.quickgdx.game.mechanics;\n" +
                "public class World {\n" +
                "    public GameplayScreen gameplayScreen;\n" +
                "    public NewTypeAddedForTestssake ntaft;" +
                "\n" +
                "    public World(GameplayScreen gameplayScreen) {\n" +
                "        this.gameplayScreen = gameplayScreen;\n" +
                "        this.ntaft = new NewTypeAddedForTestssake();\n" +
                "    }\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);
        //System.out.println(dependencyChanges.getDependencyChangeOverview());
        // StringUtils usage:
        // TYPE usage in static class access
        // CALL on  StringUtils.abbreviate(...)
        //Assert.assertTrue(containsClassDependency(dependencyChanges, "com.vogella.tasks.ui.parts.Muller", Type.ADDED));
//        // Unused dependency will not be shown
//        Assert.assertTrue(countClassDependency(dependencyChanges, "fake.io.PrintWriter", Type.ADDED) == 0);

    }

    @Test
    //TODO rewrite
    public void testAnonClasses(){
        /**
         * Only Interface gets analyzed!
         */
        String src = "package test.dependency;\n" +
                "\n" +
                "//Java program to demonstrate Anonymous inner class\n" +
                "interface Age\n" +
                "{\n" +
                "    int x = 21;\n" +
                "    void getAge();\n" +
                "}\n" +
                "class AnonymousDemo\n" +
                "{\n" +
                "    public static void main(String[] args) {\n" +
                "\n" +
                "        // Myclass is hidden inner class of Age interface\n" +
                "        // whose name is not written but an object to it\n" +
                "        // is created.\n" +
                "        Age oj1 = new Age() {\n" +
                "            @Override\n" +
                "            public void getAge() {\n" +
                "                // printing  age\n" +
                "                System.out.print(\"Age is \"+x);\n" +
                "            }\n" +
                "        };\n" +
                "        oj1.getAge();\n" +
                "    }\n" +
                "}\n";
        String dst = "package test.dependency;\n" +
                "\n" +
                "//Java program to demonstrate Anonymous inner class\n" +
                "interface Age\n" +
                "{\n" +
                "    int x = 21;\n" +
                "    void getAge();\n" +
                "}\n" +
                "class AnonymousDemo\n" +
                "{\n" +
                "    public static void main(String[] args) {\n" +
                "\n" +
                "        // Myclass is hidden inner class of Age interface\n" +
                "        // whose name is not written but an object to it\n" +
                "        // is created.\n" +
                "        Age oj1 = new Age() {\n" +
                "            @Override\n" +
                "            public void getAge() {\n" +
                "                // printing  age\n" +
                "                System.out.print(\"Age is \"+x);\n" +
                "            }\n" +
                "        };\n" +
                "        oj1.getAge();\n" +
                "    }\n" +
                "}\n";

        System.out.println(dumpStringOverview(src,dst));
    }


    @Test
    //TODO rewrite
    public void testCastTo(){
        String src = "package test.dependency;\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "        Object integer = (Integer)1;\n" +
                "}";
        String dst = "package test.dependency;\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "        Object integer = (Integer)1;\n" +
                "}";
        System.out.println(dumpStringOverview(src,dst));
    }


    @Test
    //TODO rewrite
    public void testInstanceOf(){
        String src = "package test.dependency;\n" +
                "\n" +
                "class Simple1{\n" +
                "    public static void main(String args[]){\n" +
                "        Simple1 s=new Simple1();\n" +
                "        System.out.println(s instanceof Simple1);//true  \n" +
                "    }\n" +
                "}  ";
        String dst = "package test.dependency;\n" +
                "\n" +
                "class Simple1{\n" +
                "    public static void main(String args[]){\n" +
                "        Simple1 s=new Simple1();\n" +
                "        System.out.println(s instanceof Simple1);//true  \n" +
                "    }\n" +
                "}  ";
        System.out.println(dumpStringOverview(src,dst));
    }


    @Test
    public void testSbAppendSeed(){
        String src = "package test.dependency;\n" +
                "import java.util.concurrent.atomic.AtomicLong;\n" +
                "\n" +
                "/**\n" +
                " * Generator for Globally unique Strings.\n" +
                " */\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "    private String seed;\n" +
                "    private AtomicLong sequence = new AtomicLong(1);\n" +
                "    private int length;\n" +
                "\n" +
                "    public synchronized String generateId() {\n" +
                "        StringBuilder sb = new StringBuilder(length);\n" +
                "        sb.append(sequence);\n" +
                "        sb.append(sequence.getAndIncrement());\n" +
                "        return sb.toString();\n" +
                "    }\n" +
                "}";
        String dst = "package test.dependency;\n" +
                "import java.util.concurrent.atomic.AtomicLong;\n" +
                "\n" +
                "/**\n" +
                " * Generator for Globally unique Strings.\n" +
                " */\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "    private String seed;\n" +
                "    private AtomicLong sequence = new AtomicLong(1);\n" +
                "    private int length;\n" +
                "\n" +
                "    public synchronized String generateId() {\n" +
                "        StringBuilder sb = new StringBuilder(length);\n" +
                "        sb.append(seed);\n" +
                "        sb.append(sequence.getAndIncrement());\n" +
                "        return sb.toString();\n" +
                "    }\n" +
                "}";
        System.out.println(dumpStringOverview(src,dst));
    }


    @Test
    public void returnValueNotRecognized(){
        String src = "package test.dependency;\n" +
                "import java.util.concurrent.atomic.AtomicLong;\n" +
                "\n" +
                "/**\n" +
                " * Generator for Globally unique Strings.\n" +
                " */\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "    private String hostName;\n" +
                "    public static String getHostName() {\n" +
                "        return hostName;\n" +
                "    }" +
                "}";
        String dst = "package test.dependency;\n" +
                "import java.util.concurrent.atomic.AtomicLong;\n" +
                "\n" +
                "/**\n" +
                " * Generator for Globally unique Strings.\n" +
                " */\n" +
                "\n" +
                "public class IsThisJavaCodeValidClass {\n" +
                "    private String hostName;\n" +
                "    public static String getHostName() {\n" +
                "        return hostName;\n" +
                "    }" +
                "}";
        System.out.println(dumpStringOverview(src,dst));
    }

    @Test
    public void selfDependencyInAnonClassInCall(){
        String src = "package org.apache.activemq.store.kahadb.disk.page;\n" +
                "import java.io.*;\n" +
                "\n" +
                "public class TransactionV2 {\n" +
                "    \n" +
                "    \n" +
                "    public InputStream openInputStream(final Page p) throws IOException {\n" +
                "\n" +
                "        return new InputStream() {\n" +
                "\n" +
                "\n" +
                "            @Override\n" +
                "            public int read() throws IOException {\n" +
                "                return 0;\n" +
                "            }\n" +
                "\n" +
                "            public int read(byte[] b) throws IOException {\n" +
                "                return read(b, 0, b.length);\n" +
                "            }\n" +
                "\n" +
                "            public int read(byte b[], int off, int len) throws IOException {\n" +
                "              return 1;\n" +
                "            }\n" +
                "\n" +
                "\n" +
                "        };\n" +
                "    }\n" +
                "\n" +
                "}\n";
        System.out.println(dumpStringOverview(src,src));
    }

    @Test
    public void updateAsInsertedDepTest() {
        String src = "package at.aau.difftest;\n" +
                "\n" +
                "public class MyApplication extends WowClass {\n" +
                "}";
        String dst = "package at.aau.difftest;\n" +
                "public class MyApplication extends WowClasser {\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);


        List<NodeDependency> insertedNodeDependencies = dependencyChanges.getAllInsertedNodeDependencies();
        // updated dependencies should be included in InsertedNodeDependencies!
        Assert.assertEquals(1, insertedNodeDependencies.size());
        Assert.assertEquals(insertedNodeDependencies.get(0).getDependency().getDependentOnClass(), "at.aau.difftest.WowClasser");
    }

    @Test
    public void updateAsDeletedDepTest() {
        String src = "package at.aau.difftest;\n" +
                "\n" +
                "public class MyApplication extends WowClass {\n" +
                "}";
        String dst = "package at.aau.difftest;\n" +
                "public class MyApplication extends WowClasser {\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);

        List<NodeDependency> deletedNodeDependencies = dependencyChanges.getAllDeletedNodeDependencies();
        // updated dependencies should be included in InsertedNodeDependencies!
        Assert.assertEquals(1, deletedNodeDependencies.size());
        Assert.assertEquals(deletedNodeDependencies.get(0).getDependency().getDependentOnClass(), "at.aau.difftest.WowClass");
    }

    @Test
    public void moveIsNoDeletedDepTest() {
        //language=JAVA
        String src = "package at.aau.difftest;\n" +
                "\n" +
                "public class MyApplication  {\n" +
                "    public void wow() {\n" +
                "        String.valueOf(42);\n" +
                "    }\n" +
                "\n" +
                "    public void moveHere() {\n" +
                "        //nothing\n" +
                "    }\n" +
                "}";
        //language=JAVA
        String dst = "package at.aau.difftest;\n" +
                "\n" +
                "public class MyApplication  {\n" +
                "    public void wow() {\n" +
                "        \n" +
                "    }\n" +
                "\n" +
                "    public void moveHere() {\n" +
                "        String.valueOf(42);\n" +
                "    }\n" +
                "}";

        DependencyChanges dependencyChanges = classifyDependencies(src, dst, true);

        List<NodeDependency> deletedNodeDependencies = dependencyChanges.getAllDeletedNodeDependencies();
        // updated dependencies should be included in InsertedNodeDependencies!
        Assert.assertTrue(deletedNodeDependencies.isEmpty());
    }
}
