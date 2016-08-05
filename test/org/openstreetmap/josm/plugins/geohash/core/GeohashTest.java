package org.openstreetmap.josm.plugins.geohash.core;

import net.exfidefortis.map.BoundingBox;
import net.exfidefortis.map.Latitude;
import net.exfidefortis.map.Longitude;
import net.exfidefortis.map.Point;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Mihai Chintoanu
 */
public class GeohashTest {

    @Test
    public void testBounds() {
        expectBoundsForCode(-90, -180, 90, 180, "");
        expectBoundsForCode(45, -180, 90, -135, "b");
        expectBoundsForCode(65.566406, -151.171875, 65.742187, -150.820313, "best");
        expectBoundsForCode(-26.71875, -29.53125, -25.3125, -28.125, "777");
    }

    private void expectBoundsForCode(final double south, final double west, final double north, final double east,
            final String code) {
        final Geohash geohash = new Geohash(code);
        final double delta = 0.00001;
        Assert.assertEquals(north, geohash.bounds().north().asDegrees(), delta);
        Assert.assertEquals(south, geohash.bounds().south().asDegrees(), delta);
        Assert.assertEquals(east, geohash.bounds().east().asDegrees(), delta);
        Assert.assertEquals(west, geohash.bounds().west().asDegrees(), delta);
    }

    @Test
    public void testChildren() {
        final Geohash geohash = new Geohash("b");
        Assert.assertTrue(geohash.children().contains(new Geohash("be")));
        Assert.assertFalse(geohash.children().contains(new Geohash("best")));
    }

    @Test
    public void testEquals() {
        final String code = "b";
        final Point location = new Point(Longitude.forDegrees(-151), Latitude.forDegrees(65.6));
        final Geohash forCode = new Geohash(code);

        Assert.assertEquals(forCode, new Geohash(code));
        Assert.assertFalse(forCode.equals(new Geohash("y")));
        Assert.assertEquals(forCode, new Geohash(location, 1));
        Assert.assertFalse(forCode.equals(new Geohash(location, 4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeohash_invalidCode() {
        new Geohash("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeohash_Location_negativeResolution() {
        new Geohash(new Point(Longitude.ZERO, Latitude.ZERO), -1);
    }

    @Test
    public void testGeohash_Location_Resolution() {
        expectCodeForLocationAndResolution("b", 65.6, -151, 1);
        expectCodeForLocationAndResolution("best", 65.6, -151, 4);
        expectCodeForLocationAndResolution("777", -25.5, -29, 3);
    }

    private void expectCodeForLocationAndResolution(final String code, final double latitude, final double longitude,
            final int resolution) {
        final Point location = new Point(Longitude.forDegrees(longitude), Latitude.forDegrees(latitude));
        final Geohash geohash = new Geohash(location, resolution);
        Assert.assertEquals(code, geohash.code());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeohash_nullCode() {
        new Geohash(null);
    }

    @Test(expected = NullPointerException.class)
    public void testGeohash_nullLocation_Resolution() {
        new Geohash(null, 1);
    }

    @Test
    public void testParent() {
        Assert.assertNull(Geohash.WORLD.parent());
        final Geohash geohash = new Geohash("b");
        Assert.assertEquals(Geohash.WORLD, geohash.parent());
    }

    @Test
    public void testWorld() {
        final BoundingBox bounds = Geohash.WORLD.bounds();
        Assert.assertEquals(Longitude.MINIMUM, bounds.west());
        Assert.assertEquals(Longitude.MAXIMUM, bounds.east());
        Assert.assertEquals(Latitude.MINIMUM, bounds.south());
        Assert.assertEquals(Latitude.MAXIMUM, bounds.north());
    }
}
