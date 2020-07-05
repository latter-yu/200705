import java.io.*;

public class IODemo2 {
    public static void main1(String[] args) throws IOException {
        copyFile2();
    }

    private static void copyFile() throws IOException {
        // 需要创建的实例是 BufferedInputStream 和 BufferedOutputStream
        // 要创建这样的实例, 需要先创建 FileInputStream 和 FileOutputStream
        FileInputStream fileInputStream = new FileInputStream("d:/test_dir/rose.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("d:/test_dir/rose2.jpg");
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = bufferedInputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, length);
        }
        // 此处涉及到四个流对象
        // 调用这一组 close 时, 就会自动关闭内部包含的 FileInputStream 和 FileOutputStream
        // 此处不需要写 四次 关闭
        bufferedInputStream.close();
        bufferedOutputStream.close();
        // fileInputStream.close();
        // 这里不就相当于是调用两次 close 了么? 阅读原码就会发现, close 两次不会有副作用.
    }

    private static void copyFile2() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("d:/test_dir/rose.jpg"));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("d:/test_dir/rose2.jpg"))) {
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                System.out.println("len: " + len);
                bufferedOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile3() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("d:/test_dir/rose.jpg"));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("d:/test_dir/rose2.jpg"))) {
            int len = -1;
            byte[] buffer = new byte[1024];
            while (true) {
                len = bufferedInputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                bufferedOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        // 分别不使用缓冲区和使用缓冲区来进行读取一个大文件操作, 感受时间上的差异.
        // testNoBuffer();
        testBuffer();
    }

    private static void testNoBuffer() {
        // 读的时候就是一个字节一个字节的读, 完全不是用任何缓冲区.
        long beg = System.currentTimeMillis();
        try(FileInputStream fileInputStream = new FileInputStream("d:/sky.jpeg")) {
            int ch = -1;
            while ((ch = fileInputStream.read()) != -1) {
                // 什么都不干
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("no buffer: " + (end - beg) + " ms");
    }

    private static void testBuffer() {
        long beg = System.currentTimeMillis();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("d:/sky.jpeg"))) {
            int ch = -1;
            while ((ch = bufferedInputStream.read()) != -1) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("has buffer: " + (end - beg) + " ms");
    }

    public static void main3(String[] args) {
        // 字符流 a, b, c
        copyFilea();
    }

    private static void copyFilea() {
        // 处理文本文件, 需要使用字符流.
        try (FileReader fileReader = new FileReader("d:/test_dir/test.txt");
             FileWriter fileWriter = new FileWriter("d:/test_dir/test2.txt")) {
            char[] buffer = new char[1024];
            int len = -1;
            while ((len = fileReader.read(buffer)) != -1) {
                fileWriter.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFileb() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("d:/test_dir/test.txt"));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("d:/test_dir/test2.txt"))) {
            char[] buffer = new char[1024];
            int len = -1;
            while ((len = bufferedReader.read(buffer)) != -1) {
                bufferedWriter.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFilec() {
        // 带缓冲区的字符流中有一种特殊的用法, 按行读取.
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("d:/test_dir/test.txt"));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("d:/test_dir/test2.txt"))) {
            String line = "";
            // readLine 表示读一行. 读到换行符为止. 如果读取文件完毕, 就会返回 null
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("line: " + line);
                bufferedWriter.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Student implements Serializable {
        public String name;
        public int age;
        public int score;
    }

    public static class IODemo6 {
        public static void main(String[] args) throws IOException, ClassNotFoundException {
            /*Student s = new Student();
            s.name = "张三";
            s.age = 20;
            s.score = 100;
            serializeStudent(s);*/

            Student s = deserializeStudent();
            System.out.println(s.name);
            System.out.println(s.age);
            System.out.println(s.score);
        }

        private static void serializeStudent(Student s) throws IOException {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("d:/student.txt"));
            // 这个 writeObject 集序列化+写文件 两者同时搞定
            objectOutputStream.writeObject(s);
            objectOutputStream.close();
        }

        private static Student deserializeStudent() throws IOException, ClassNotFoundException {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("d:/student.txt"));
            Student s = (Student) objectInputStream.readObject();
            return s;
        }
    }
}
