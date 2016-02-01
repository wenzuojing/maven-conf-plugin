package org.apache.maven.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.Properties;

/**
 * Created by wens on 15-3-24.
 */

public abstract class AbstractConf extends AbstractMojo {

    @Parameter( property = "env", defaultValue = "src/main/conf-env" )
    private String evn;
    @Parameter( property = "input", defaultValue = "src/main/conf" )
    private String input ;
    @Parameter( property = "output", defaultValue = "src/main/resources" )
    private String output ;

    @Parameter(property = "basedir" , defaultValue = "${project.basedir}")
    private String baseDir ;

    protected File envFile;
    protected File inputFile ;
    protected File outputFile ;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        prepare() ;
        goal();
    }

    protected abstract void goal() throws MojoExecutionException ;


    private void prepare() throws MojoExecutionException {
        evn = getAbstractPath(evn);
        input = getAbstractPath(input);
        output = getAbstractPath(output);

        envFile = new File(evn) ;

        if(!envFile.exists()){
            String err  =  evn + " is not exists." ;
            throw new  MojoExecutionException(err) ;
        }

        inputFile  = new File(input) ;

        if(!inputFile.exists()){
            getLog().info("Ignore evn,because "+ input +" is not exists.");
            return ;
        }

        outputFile = new File(output);

        //cleanDir(outputFile);

        if(!outputFile.exists()){
            outputFile.mkdirs() ;
        }
    }

    private void cleanDir(File dir) {
       if(dir.isDirectory()){
           deleteDir(dir);
       }
    }

    private  void deleteDir(File dir) {
        File[] files = dir.listFiles();
        for(File file : files ){

            if(file.isFile() ){
                file.delete() ;
            }else{
                deleteDir(file) ;
            }
        }

        dir.delete();
    }



    protected Properties loadProperties(File filterFile) throws MojoExecutionException {
        Properties properties = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(filterFile);
            properties.load(input);
        } catch (Exception e) {
            throw new MojoExecutionException("Load properties's file fail." ,e) ;
        }finally {
            if(input != null ){
                try {
                    input.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return properties;
    }

    protected void doFilter(Properties properties , File inputFile, File outputFile) throws MojoExecutionException {

        String[] files = inputFile.list() ;

        for(String f : files ){
            File in  = new File(inputFile , f ) ;
            File out  = new File(outputFile , f ) ;
            if(in.isDirectory()){

                if( in.isHidden() ){
                    continue;
                }

                if(!out.exists()){
                    out.mkdirs() ;
                }
                doFilter(properties , in , out );
            }else {
                doReplace(properties , in  , out ) ;
            }
        }


    }

    protected void doReplace(Properties properties, File in, File out) throws MojoExecutionException {

        if(out.exists()){
            out.delete();
        }

        FileReader fileReader = null ;

        BufferedReader br = null ;

        FileOutputStream outputStream = null ;

        try{
            fileReader = new FileReader(in) ;
            br = new BufferedReader(fileReader);

            StringBuilder sb  = new StringBuilder((int)in.length());

            while (true){
                String line = br.readLine();
                if(line == null) {
                    break;
                }

                if(line.length() > 0 ){
                    line = replacePlaceholder(properties, line);
                }
                sb.append(line).append(System.getProperty("line.separator"));
            }

            outputStream = new FileOutputStream(out);
            outputStream.write(sb.toString().getBytes());

        }catch (Exception e){
            throw new MojoExecutionException("Replace fail." , e ) ;
        }finally {
            if(br != null ){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fileReader != null ){
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(outputStream != null ){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected  String replacePlaceholder(Properties properties, String line) {
        int s = line.indexOf("${");

        if(s == -1 ) return line;

        int e = line.indexOf("}" , s + 1 ) ;

        if(e == -1 ) return line;

        String placeholder = line.substring(s +2,e).trim() ;

        line = line.substring(0 , s ) + properties.get(placeholder)+ line.substring(e + 1 ) ;

        return replacePlaceholder(properties , line);
    }


    protected String getAbstractPath( String srcPath) {
        if(!srcPath.startsWith(baseDir)){
            if(!srcPath.startsWith(File.separator)){
                srcPath = File.separator + srcPath ;
            }
            srcPath = baseDir + srcPath ;
        }
        return srcPath ;
    }
}
