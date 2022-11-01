/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.testing.arquillian.utils;

import com.kumuluz.ee.testing.arquillian.assets.MainWrapper;
import com.kumuluz.ee.testing.arquillian.exceptions.ParsingException;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;

import java.io.*;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

/**
 * Processes output received from KumuluzEE Server process (generated by {@link MainWrapper}).
 *
 * @author Urban Malc
 * @since 1.0.0
 */
public class OutputProcessor implements Runnable, Closeable {

    private InputStream stream;
    private CountDownLatch latch;

    private HTTPContext httpContext;
    private Throwable processingError;
    private Throwable deploymentError;

    public OutputProcessor(InputStream stream, CountDownLatch latch) {
        this.stream = stream;
        this.latch = latch;
    }

    public HTTPContext getHttpContext() {
        return httpContext;
    }

    public Throwable getProcessingError() {
        return processingError;
    }

    public Throwable getDeploymentError() {
        return deploymentError;
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.stream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(MainWrapper.MSG_PREFIX)) {
                    processMessage(line.trim());
                } else {
                    System.out.println(line);
                }
            }
        } catch (IOException | ParsingException e) {
            this.processingError = e;
            this.latch.countDown();
        }
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    private void processMessage(String message) throws ParsingException {
        if (message.equals(MainWrapper.MSG_SERVER_STARTED)) {
            this.latch.countDown();
        } else if (message.startsWith(MainWrapper.MSG_METADATA_PREFIX)) {
            String metadata = message.substring(MainWrapper.MSG_METADATA_PREFIX.length());

            String[] tokens = metadata.split("\t");

            int port;
            try {
                port = Integer.parseInt(tokens[0]);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                throw new ParsingException("Could not parse port from string: " + metadata, e);
            }

            httpContext = new HTTPContext("localhost", port);

            for (int i = 1; i < tokens.length; i++) {
                if (!tokens[i].isEmpty()) {
                    String[] servletInfo = tokens[i].split(":");

                    if (servletInfo.length < 1) {
                        throw new ParsingException("Could not parse servlet information from token: " + tokens[i]);
                    }

                    httpContext.add(new Servlet(servletInfo[0], (servletInfo.length > 1) ? servletInfo[1] : ""));
                }
            }
        } else if (message.startsWith(MainWrapper.MSG_EXCEPTION_PREFIX)) {
            try {
                byte[] serialized = Base64.getDecoder().decode(
                        message.substring(MainWrapper.MSG_EXCEPTION_PREFIX.length()).getBytes());
                ByteArrayInputStream bi = new ByteArrayInputStream(serialized);
                ObjectInputStream si = new ObjectInputStream(bi);
                this.deploymentError = (Exception) si.readObject();
                this.latch.countDown();
            } catch (IOException | ClassNotFoundException e1) {
                throw new ParsingException("Error while deserializing exception.", e1);
            }

        } else {
            throw new ParsingException("Could not parse message: " + message);
        }
    }
}
