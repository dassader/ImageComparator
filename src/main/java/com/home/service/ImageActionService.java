package com.home.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageActionService {
    String compare(InputStream inputStream, InputStream inputStream1) throws IOException;
}
