package model.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import model.DirItem;
import model.FileSystem;

class FileSystemTest {

	@Test
	void testCreateFile() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateDir() throws Exception {
		FileSystem fileSystem = new FileSystem();
		DirItem dirItem = assertDoesNotThrow(() -> {
			return fileSystem.createDir("123", DirItem.DIR, "/");
		});
		
		assertEquals("123", dirItem.getName());
		
	}

	@Test
	void testOpen() {
		fail("Not yet implemented");
	}

	@Test
	void testWrite() {
		fail("Not yet implemented");
	}

	@Test
	void testGetFileTree() {
		fail("Not yet implemented");
	}

	@Test
	void testGetFileTreeDirItem() {
		fail("Not yet implemented");
	}

	@Test
	void testGetFileTreeString() {
		fail("Not yet implemented");
	}

	@Test
	void testClose() {
		fail("Not yet implemented");
	}

	@Test
	void testGetOpenedFile() {
		fail("Not yet implemented");
	}

	@Test
	void testSetOpenedFile() {
		fail("Not yet implemented");
	}

}
