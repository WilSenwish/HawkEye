package com.littleyes.common.core;

/**
 * <p> <b> Plugin Not Found Exception </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-08-17
 */
public class PluginNotFoundException extends RuntimeException {
    public PluginNotFoundException(String message) {
        super(message);
    }
}
