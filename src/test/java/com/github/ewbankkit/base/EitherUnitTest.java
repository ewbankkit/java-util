//
// Kit's Java Utils.
//

package com.github.ewbankkit.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EitherUnitTest {
    @Test
    public void eitherTest1() {
        Either<String, Integer> either = Either.left("BOOP");
        assertTrue(either.isLeft());
        assertFalse(either.isRight());
    }

    @Test
    public void eitherTest2() {
        Either<String, Integer> either = Either.right(42);
        assertFalse(either.isLeft());
        assertTrue(either.isRight());
    }

    @Test
    public void eitherTest3() {
        Either<String, String> either = Either.left("BOOP");
        assertTrue(either.isLeft());
        assertFalse(either.isRight());
    }

    @Test
    public void eitherTest4() {
        Either<String, String> either = Either.right("GOOP");
        assertFalse(either.isLeft());
        assertTrue(either.isRight());
    }

    @Test
    public void eitherTest5() {
        Either<String, Integer> either = Either.left("BOOP");
        assertEquals("BOOP", either.getLeft());
    }

    @Test
    public void eitherTest6() {
        Either<String, Integer> either = Either.right(42);
        assertEquals(42, either.getRight().intValue());
    }

    @Test
    public void eitherTest7() {
        Either<String, String> either = Either.left("BOOP");
        assertEquals("BOOP", either.getLeft());
    }

    @Test
    public void eitherTest8() {
        Either<String, String> either = Either.right("GOOP");
        assertEquals("GOOP", either.getRight());
    }

    @Test(expected = IllegalStateException.class)
    public void eitherTest9() {
        Either<String, Integer> either = Either.left("BOOP");
        assertEquals(42, either.getRight().intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void eitherTest10() {
        Either<String, Integer> either = Either.right(42);
        assertEquals("BOOP", either.getLeft());
    }
}
