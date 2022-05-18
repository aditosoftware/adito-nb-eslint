package de.adito.aditoweb.nbm.eslint.impl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author s.seemann, 18.05.2022
 */
class ESLintInitTest
{
  /**
   * Tests reflection which is used in {@link ESLintInit}
   */
  @Test
  void testReflection() throws ClassNotFoundException, NoSuchFieldException
  {
    Class<?> clazz = Class.forName("org.openide.loaders.DataObject$DOSavable");
    assertNotNull(clazz);
    Field obj = clazz.getDeclaredField("obj");
    assertNotNull(obj);
  }
}