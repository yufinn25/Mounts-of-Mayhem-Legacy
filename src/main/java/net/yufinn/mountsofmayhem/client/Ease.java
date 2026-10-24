package net.yufinn.mountsofmayhem.client;

import net.minecraft.util.Mth;

public final class Ease {
    private Ease() {}

    public static float inOutSine(float x) {
        return -(Mth.cos((float) Math.PI * x) - 1.0F) / 2.0F;
    }

    public static float outBack(float x) {
        return 1.0F + 2.70158F * cube(x - 1.0F) + 1.70158F * square(x - 1.0F);
    }

    public static float outQuart(float x) {
        return 1.0F - square(square(1.0F - x));
    }

    public static float outCubic(float x) {
        return 1.0F - cube(1.0F - x);
    }

    public static float inOutExpo(float x) {
        if (x < 0.5F) {
            return x == 0.0F ? 0.0F : (float) (Math.pow(2.0D, 20.0D * x - 10.0D) / 2.0D);
        }
        return x == 1.0F ? 1.0F : (float) ((2.0D - Math.pow(2.0D, -20.0D * x + 10.0D)) / 2.0D);
    }

    public static float inQuad(float x) {
        return x * x;
    }

    public static float outCirc(float x) {
        return (float) Math.sqrt(1.0F - square(x - 1.0F));
    }

    public static float inCirc(float x) {
        return (float) -Math.sqrt(1.0F - x * x) + 1.0F;
    }

    public static float inOutElastic(float x) {
        if (x == 0.0F) return 0.0F;
        if (x == 1.0F) return 1.0F;
        double sin = Math.sin((20.0D * x - 11.125D) * 1.3962635F);
        return x < 0.5F
                ? (float) (-(Math.pow(2.0D, 20.0D * x - 10.0D) * sin) / 2.0D)
                : (float) (Math.pow(2.0D, -20.0D * x + 10.0D) * sin / 2.0D + 1.0D);
    }

    public static float inOutBack(float x) {
        if (x < 0.5F) {
            return 4.0F * x * x * (7.189819F * x - 2.5949094F) / 2.0F;
        }
        float dt = 2.0F * x - 2.0F;
        return (dt * dt * (3.5949094F * dt + 2.5949094F) + 2.0F) / 2.0F;
    }

    private static float square(float x) {
        return x * x;
    }

    private static float cube(float x) {
        return x * x * x;
    }
}
