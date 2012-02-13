/*
 * Copyright 2008 the original author or authors. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.constretto.model;

import org.constretto.exception.ConstrettoException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:kaare.nilsen@gmail.com">Kaare Nilsen</a>
 */
public class UrlResource extends Resource {
    public UrlResource(String path) {
        super(path);
    }

    @Override
    public boolean exists() {
        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection con = (HttpURLConnection) new URL(path).openConnection();
            con.setRequestMethod("HEAD");
            int responseCode = con.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            URL url = new URL(path);
            return url.openStream();
        } catch (MalformedURLException ex) {
            throw new ConstrettoException("Could not load URL. Path tried: [" + path + "]", ex);
        } catch (IOException e) {
            throw new ConstrettoException("Woot", e);
        }
    }
}
