package ch.dragbone.anonsomenauts;

import static org.junit.Assert.*;

import org.junit.Test;

public class BitDataTest{

	@Test
	public void findFirstTest(){
		BitData bd = new BitData(new boolean[]{true, true, false, true, false, false, true});

		assertTrue(bd.findFirst(new boolean[]{true}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true, false}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true, false, true}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true, false, true, false}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true, false, true, false, false}) == 0);
		assertTrue(bd.findFirst(new boolean[]{true, true, false, true, false, false, true}) == 0);

		assertTrue(bd.findFirst(new boolean[]{true, false}) == 1);
		assertTrue(bd.findFirst(new boolean[]{true, false, true}) == 1);
		assertTrue(bd.findFirst(new boolean[]{true, false, true, false}) == 1);
		assertTrue(bd.findFirst(new boolean[]{true, false, true, false, false}) == 1);
		assertTrue(bd.findFirst(new boolean[]{true, false, true, false, false, true}) == 1);

		assertTrue(bd.findFirst(new boolean[]{false}) == 2);
		assertTrue(bd.findFirst(new boolean[]{false, true}) == 2);
		assertTrue(bd.findFirst(new boolean[]{false, true, false}) == 2);
		assertTrue(bd.findFirst(new boolean[]{false, true, false, false}) == 2);
		assertTrue(bd.findFirst(new boolean[]{false, true, false, false, true}) == 2);

		assertTrue(bd.findFirst(new boolean[]{true, false, false}) == 3);
		assertTrue(bd.findFirst(new boolean[]{true, false, false, true}) == 3);

		assertTrue(bd.findFirst(new boolean[]{false, false}) == 4);
		assertTrue(bd.findFirst(new boolean[]{false, false, true}) == 4);

		assertTrue(bd.findFirst(new boolean[]{false, true, true}) == -1);
		assertTrue(bd.findFirst(new boolean[]{true, true, true}) == -1);
		assertTrue(bd.findFirst(new boolean[]{false, true, false, true}) == -1);
	}
}
