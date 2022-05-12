package com.fx.pan.factory.operation.write;

import com.fx.pan.factory.operation.write.domain.WriteFile;

import java.io.InputStream;

/**
 * @author leaving
 * @date 2022/3/19 16:29
 * @version 1.0
 */

public abstract class Writer {
    public Writer() {
    }

    public abstract void write(InputStream inputStream, WriteFile writeFile);
}
