package java.lang;


import com.yl.test.DemoClass;

import java.io.InputStream;

/**
 * @author Alex
 * @since 2019/4/8 14:54
 */
public class ClassLoaderDemo {

    public static void main(String[] args) throws Exception{

        ClassLoader loader = new ClassLoader(){
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".")+1) + ".class";

                    InputStream inputStream = getClass().getResourceAsStream(fileName);

                    if(inputStream == null){
                        return super.loadClass(name);
                    }

                    byte[] bytes = new byte[inputStream.available()];

                    inputStream.read(bytes);

                    return defineClass(name, bytes, 0, bytes.length);
                }catch (Exception e){
                    throw new ClassNotFoundException(e.toString());
                }
            }
        };

        Class<?> clazz = loader.loadClass("java.lang.Demo");

        System.err.println(clazz.newInstance() instanceof DemoClass);
    }

}
