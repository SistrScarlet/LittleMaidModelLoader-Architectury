package net.sistr.littlemaidmodelloader.client.util;

import java.nio.FloatBuffer;

public interface Matrix4fAccessor {
    void readColumnMajor(FloatBuffer buf);
}
