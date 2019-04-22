package com.cherkavi.productconfig.cd.uploader.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface MutlipartStorage {

    void init();

    void store(MultipartFile file);

    void store(MultipartFile file, String outputFileName);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
