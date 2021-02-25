package com.netcracker.edu.project.user.project.tree;

import com.netcracker.edu.project.user.exception.FileException;
import com.netcracker.edu.project.user.project.Directory;
import com.netcracker.edu.project.user.project.File;
import com.netcracker.edu.project.user.tree.Tree;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TreeTest {

    private Tree<File> tree;

    @Before
    public void beforeTest() {
        this.tree = new Tree<>(new Directory("a", "/a"));
    }

    @Test
    public void Should_SuccessfulAddFile_When_FileUnique() throws FileException {
        File file = new File("b", "/a/b", "TEXT");

        tree.add(file);

        String actualContent = tree.find("/a/b").getContent();
        assertEquals("TEXT", actualContent);
    }

    @Test(expected = FileException.class)
    public void Should_ThrowException_When_AddDuplicateFiles() throws FileException {
        File file = new File("b", "/a/b", "TEXT");
        File file2 = new File("b", "/a/b", "TEXT");

        tree.add(file);
        tree.add(file2);
    }

    @Test(expected = FileException.class)
    public void Should_ThrowException_When_AddDuplicateDirectory() throws FileException {
        File directory = new Directory("b", "/a/b");
        File directory2 = new Directory("b", "/a/b");

        tree.add(directory);
        tree.add(directory2);
    }

    @Test(expected = FileException.class)
    public void Should_ThrowException_When_TryingToFind_When_FileWasDeletedSuccessfully() throws FileException {
        tree.add(new Directory("b", "/a/b"));
        tree.add(new File("a.txt", "/a/b/a.txt", "TEXT"));

        tree.delete("/a/b/a.txt");

        tree.find("/a/b/a.txt");
    }

    @Test(expected = FileException.class)
    public void Should_ThrowException_When_TryingToFindFileInDirectory_When_DirectoryWasDeletedSuccessfully() throws FileException {
        tree.add(new Directory("b", "/a/b"));
        tree.add(new File("a.txt", "/a/b/a.txt", "TEXT"));

        tree.delete("/a/b");

        tree.find("/a/b/a.txt");
    }
}
