package org.apache.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Created by wens on 15-3-25.
 */
@Mojo( name = "clean")
public class Clean extends AbstractConf {
    
    protected void goal() throws MojoExecutionException {

        //do nothing

    }
}
