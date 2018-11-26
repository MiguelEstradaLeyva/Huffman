/**
 * This is the huffman project
 * @author miguelestrada
 * due the 26th
*/
import java.io.File;
import java.io.*;
import java.io.RandomAccessFile;
import java.util.*;
import java.lang.String;
public class Huffman {
    public static void main(String[] arg) throws Exception {
        String filename = "mcgee.txt";
        String codefile = "codeman.txt";
        String uncomF = "uncompF.txt";
        String compFile = "compF.txt";

        encode(filename, codefile, compFile);
        decode(compFile,codefile,uncomF);
    }
    //will use this to put frequencies in
    private static TreeMap<Character,HuffmanC > theMap;
    //making a method to read in the freq
    public static TreeMap freq(String file) {
        TreeMap<Character, HuffmanC> theMap = new TreeMap<>();
        //read in file code given to us by prof Hansen
        try{
        RandomAccessFile fin = new RandomAccessFile(new File(file), "r");//"mcgee.txt"), "r");
        int b = fin.read();
        char c = (char) b;
        while (b != -1) {
            //System.out.println((char)b + "\t" + b);
            //if is first occurance put in map
            if (theMap.get(c) == null) {
                theMap.put((char) b, new HuffmanC(((char) b)));
                //else add the frequency
            } else
                   theMap.get((c)).increaseFrequency();
                b = fin.read();
                c = (char) b;
            }
            fin.close();
        }catch (Exception e){
         System.out.println("error");
         System.out.println(e);
        }
        return theMap;
        }
    //create tree map and put in priority Q to use later
    public static PriorityQueue<TreeNode> Huffman(PriorityQueue<HuffmanC> queue) {
        PriorityQueue<TreeNode> queue1 = new PriorityQueue<>(1, TreeNode::compareTo);
        for (HuffmanC entry : queue) {
            TreeNode value = new TreeNode();
            value.updateHuff(entry);
            queue1.add(value); }
        int n = 257;
        for (int i = 1; i < n; i++) {
            if (queue1.size() == 1) {
                break; }
            TreeNode z = new TreeNode();
            TreeNode x = queue1.poll();
            TreeNode y = queue1.poll();
            z.left = new TreeNode(x);
            z.right = new TreeNode(y);
            z.frequency = y.frequency + x.frequency;
            queue1.add(z); }
        return queue1;
    }
    //encode the file, taking in 3 different file, original, compressed and decompressed
    public static void encode(String originalFilename, String codeFilename, String compressedFilename) {
        PriorityQueue<HuffmanC> queue;
        PriorityQueue<TreeNode> queue1;
        theMap = freq(originalFilename);
        queue = CreateTree(theMap);
        queue1 = Huffman(queue);
        System.out.println(queue1.size());
        createC(queue1);
        try{
            PrintWriter outCode = new PrintWriter(new File(codeFilename));
            for( HuffmanC huffmanC : theMap.values()){
                outCode.println(huffmanC.toString());
            }
            outCode.close();
        }catch (Exception e){
            System.out.print(e.getMessage());
        }
        BitOutputStream outFile = new BitOutputStream(compressedFilename);
        try{
            RandomAccessFile fin = new RandomAccessFile( new File(originalFilename), "r");
            int b = fin.read();
            String EncryptV = "";
            while( b != -1){
                ArrayList<Integer> EncriptC = theMap.get((char) b).code;
                for(int i = 0; i< EncriptC.size(); i++){
                    EncryptV += "" + EncriptC.get(i);
                }
                outFile.writeString(EncryptV);
                b = fin.read();
                EncryptV = "";
            }
            fin.close();
            outFile.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(queue1.size());
    }
    //perform the opposite of encode again taking in 3 files.
    public static void decode(String compressedFilename, String codeFilename, String decompressedFile) {
    TreeMap<String, Character> structureM = createM(codeFilename);
    read(structureM,decompressedFile,compressedFilename);
    }
    // organizing the tree map and put it in a priority que.
    //getting the frequency and branching out to a full tree
    public static PriorityQueue<HuffmanC> CreateTree(TreeMap<Character, HuffmanC> theMap) {
        PriorityQueue<HuffmanC> queue = new PriorityQueue<>(1, HuffmanC::compareTo);
        queue.addAll(theMap.values());
        return queue;
    }
    //takes the codefile and creates a tree map.
    // and it will return a structure map with the keys and char.
    public static TreeMap createM(String codeFilename){
        TreeMap<String, Character> structureM = new TreeMap<>();
        String input;
        try {
            File file = new File(codeFilename);
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                input = scan.nextLine();
                Scanner scan2 = new Scanner(input);
                scan2.useDelimiter(" ");

                    int a = Integer.parseInt(scan2.next());
                    String a2 = (scan2.next());
                    structureM.put(a2,(char) a);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return structureM;
    }
    // created objects that hold the frec and the characters based on the huffman algorithm.
    public static class HuffmanC implements Comparable<HuffmanC> {
        private int frequency = 0;
        private ArrayList<Integer> code = new ArrayList<>(0);
        private char value;
        //character being counted
        public HuffmanC(char val) {
            value = val;
            this.frequency = 1;
        }
        //increase freq
        public void increaseFrequency() {
            frequency++;
        }
        //comparing frequency
        @Override
        public int compareTo(HuffmanC o) {
            if (this.frequency > o.frequency) {
                return 1;
            } else if (this.frequency < o.frequency) {
                return -1;
            } else
                return 0;
        }
        //setting how a string should look like
        @Override
        public String toString() {
            String codeS = "";
            for (int i = 0; i < code.size(); i++) {
                codeS += code.get(i).toString();
            }
            return (int) this.value + " " + codeS;
        }
    }
    // tree node class makes tree Nodes
    //holds the frequency to create the tree
    public static class TreeNode implements Comparable<TreeNode> {
        private TreeNode left, right;
        private int frequency;
        private HuffmanC huffmanC;
        //default constructor
        public TreeNode() {
        }
        //making more tree nodes
        public TreeNode(TreeNode node) {
            frequency = node.frequency;
            huffmanC = node.huffmanC;
            left = node.left;
            right = node.right;
        }
        //update treenodes
        public void updateHuff(HuffmanC huffmanC) {
            this.huffmanC = huffmanC;
            frequency = huffmanC.frequency;
        }
        //how the tree nodes should be compared
        @Override
        public int compareTo(TreeNode o) {
            if (this.frequency > o.frequency) {
                return 1;
            } else if (this.frequency < o.frequency) {
                return -1;
            } else
                return 0;
        }
    }
    //helping the method createmap.
    public static void read(TreeMap<String, Character> structureM, String decomFile, String compFile){
        BitInputStream comp = new BitInputStream(compFile);
        try{
            RandomAccessFile decom = new RandomAccessFile(new File(decomFile), "rw");
            int b = comp.nextBit();
            String input = "";
            while(b !=-1){
                input += "" +b;
                if(structureM.containsKey(input)){
                    decom.write(structureM.get(input));
                    input = "";
                }
                b = comp.nextBit();
            }
        }catch (Exception e){
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
    //recursive method to help with setting up the index and array list
    public static void createC(PriorityQueue<TreeNode> queue){
        TreeNode tree = queue.poll();
        ArrayList<Integer>  data = new ArrayList<>(0);
        createC2(tree,data);
    }
    //this method goes through the file, so that a code can be assign for compression.
    public static void createC2(TreeNode node, ArrayList<Integer> data){
        if(node.huffmanC != null){
            if(theMap.get(node.huffmanC.value) != null){
                theMap.get(node.huffmanC.value).code = new ArrayList<>(data);
            }
        }if (node.left !=null){
            data.add(0);

            createC2(node.left, data);
            data.remove(data.lastIndexOf(0));
        }if(node.right != null){
            data.add(1);

            createC2(node.right, data);
            data.remove(data.lastIndexOf(1));
        }
    }
}