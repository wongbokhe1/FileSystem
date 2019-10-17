package model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import model.DirItem;
import model.FileSystem;

class FileSystemTest {

	@Test
	void testParsePath() {
		String path = "/aaa/bbb/";
		List<String> paths = FileSystem.parsePath(path);
		List<String> target = new ArrayList<String>();
		target.add("aaa");
		target.add("bbb");
		assertTrue(paths.equals(target) );
	}
	



}
