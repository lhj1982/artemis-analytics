package com.nike.artemis;

import java.io.InputStream;
import java.util.Date;

public interface RuleSourceProvider {
    Date getLastModified();

    InputStream getObjectContent();
}
