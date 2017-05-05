package org.constretto.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringResource extends Resource {

    private String content;

    public StringResource(String content) {
        super("");
        this.content = content;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
    }
}
