package org.apache.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Properties;

/**
 * Created by wens on 15-3-25.
 */
@Mojo( name = "conf", defaultPhase = LifecyclePhase.INITIALIZE )
public class Conf extends AbstractConf {

    protected void goal() throws MojoExecutionException {
        Properties properties = loadProperties(envFile);

        doFilter(properties , inputFile , outputFile );
    }
}
