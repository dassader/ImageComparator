package com.home.service;

import com.home.comparator.ImageComparator;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageActionServiceImpl implements ImageActionService {

    @Autowired
    private ImageComparator imageComparator;

    @Override
    public String compare(InputStream firstImageInputStream, InputStream secondImageInputStream) throws IOException {
        InputStream areasOfDifferentAsImage =
                imageComparator.findAreasOfDifferentAsImage(firstImageInputStream, secondImageInputStream);

        return "data:image/png;base64,"+new String(Base64.encodeBase64(IOUtils.toByteArray(areasOfDifferentAsImage)), "UTF-8");
    }
}
