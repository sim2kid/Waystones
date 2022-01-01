package com.simmgames.waystones.structure;

import org.bukkit.Location;

import java.util.Objects;

public class Vector3 {
    public double X;
    public double Y;
    public double Z;

    public static Vector3 Zero = new Vector3(0,0,0);

    public Vector3(double xPos, double yPos, double zPos)
    {
        X = xPos;
        Y = yPos;
        Z = zPos;
    }

    @Override
    public String toString() {
        return "[" + X +
                ", " + Y +
                ", " + Z +
                "]";
    }

    public double getDistance(Vector3 other)
    {
        return Math.sqrt(Math.pow(other.X - this.X, 2) + Math.pow(other.Y - this.Y, 2) + Math.pow(other.Z - this.Z, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return X == vector3.X && Y == vector3.Y && Z == vector3.Z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z);
    }

    public Vector3 round()
    {
        return new Vector3(Math.round(X), Math.round(Y), Math.round(Z));
    }
    public Vector3 floor()
    {
        return new Vector3(Math.floor(X), Math.floor(Y), Math.floor(Z));
    }

    public Vector3(Location location)
    {
        this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    public Vector3(Location location, boolean useExact)
    {
        X = useExact ? location.getX() : location.getBlockX();
        Y = useExact ? location.getY() : location.getBlockY();
        Z = useExact ? location.getZ() : location.getBlockZ();
    }
}
