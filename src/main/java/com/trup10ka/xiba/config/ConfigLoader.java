package com.trup10ka.xiba.config;

import org.jetbrains.annotations.NotNull;

/**
 * This interface was reused from the previous project Kappa
 * @see <a href="https://github.com/Trup10ka/Kappa/blob/main/src/main/java/com/trup10ka/kappa/config/ConfigLoader.java">Kappa - ConfigLoader</a>
 */
public interface ConfigLoader
{
    @NotNull
    XibaConfig loadConfig();
}
