package com.googlecode.greysanatomy;

import com.googlecode.greysanatomy.console.client.ConsoleClient;
import com.googlecode.greysanatomy.exception.PIDNotMatchException;
import com.googlecode.greysanatomy.util.HostUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class GreysAnatomyMain {

    private static final Logger logger = Logger.getLogger("greysanatomy");
    public static final String JARFILE = GreysAnatomyMain.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    public GreysAnatomyMain(String[] args) throws Exception {

        // ���������ļ�
        Configer configer = analyzeConfiger(args);

        // ����Ǳ���IP,���Լ���Agent
        if (HostUtils.isLocalHostIp(configer.getTargetIp())) {
            // ����agent
            attachAgent(configer);
        }

        // �������̨
        if (activeConsoleClient(configer)) {

//            logger.info("attach done! pid={}; host={}; JarFile={}", new Object[]{
//                    configer.getJavaPid(),
//                    configer.getTargetIp() + ":" + configer.getTargetPort(),
//                    JARFILE});


        }

    }

    /**
     * ����configer
     *
     * @param args
     * @return
     */
    private Configer analyzeConfiger(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("pid").withRequiredArg().ofType(int.class).required();
        parser.accepts("target").withOptionalArg().ofType(String.class);
        parser.accepts("multi").withOptionalArg().ofType(int.class);

        final OptionSet os = parser.parse(args);
        final Configer configer = new Configer();

        if (os.has("target")) {
            final String[] strSplit = ((String) os.valueOf("target")).split(":");
            configer.setTargetIp(strSplit[0]);
            configer.setTargetPort(Integer.valueOf(strSplit[1]));
        }

        if (os.has("multi")
                && (Integer) os.valueOf("multi") == 1) {
            configer.setMulti(true);
        } else {
            configer.setMulti(false);
        }

        configer.setJavaPid((Integer) os.valueOf("pid"));
        return configer;
    }

    /**
     * ����Agent
     *
     * @param configer
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    private void attachAgent(Configer configer) throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

        Object attachVmdObj = null;
        for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
            if (((String) vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null)).equals("" + configer.getJavaPid())) {
                attachVmdObj = obj;
            }
        }

        if (null == attachVmdObj) {
            throw new IllegalArgumentException("pid:" + configer.getJavaPid() + " not existed.");
        }

        Object vmObj = null;
        try {
            vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
            vmClass.getMethod("loadAgent", String.class, String.class).invoke(vmObj, JARFILE, configer.toString());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }

    }

    /**
     * �������̨�ͻ���
     *
     * @param configer
     * @throws Exception
     */
    private boolean activeConsoleClient(Configer configer) throws Exception {
        try {
            ConsoleClient.getInstance(configer);
            return true;
        } catch (java.rmi.ConnectException ce) {
            if(logger.isLoggable(Level.WARNING)){
                logger.warning(String.format("target{%s:%s} RMI was shutdown, console will be exit.", configer.getTargetIp(), configer.getTargetPort()));
            }
        } catch (PIDNotMatchException pidnme) {
            if(logger.isLoggable(Level.WARNING)){
                logger.warning(String.format("target{%s:%s} PID was not match, console will be exit.", configer.getTargetIp(), configer.getTargetPort()));
            }
        }
        return false;
    }


    public static void main(String[] args) {

        try {
            new GreysAnatomyMain(args);
        } catch (Throwable t) {
            if(logger.isLoggable(Level.SEVERE)){
                logger.log(Level.SEVERE,String.format("start greys-anatomy failed. because %s", t.getMessage()), t);
            }
            System.exit(-1);
        }

    }
}
