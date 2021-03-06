/**
 * Universidade Católica Dom Bosco
 * Atividade: Compactação de arquivos usando códigos de Huffman
 * Academico: Thayane Batista RA159049
 * Docente: Marcos Alves
 * Disciplina: Estrutura de dados II
 * 
 * Este trabalho vai gerar a partir de um arquivo txt que o usuario deve definir o dretorio
 * um código em binario gerado pela arvore de hufmann que também sera inserido em um arquivo txt
 * juntamente com o arquivo decodificado utilizando os valores em binario para chegar ao resultado original
 * contamos a frequencia de cada caracter dentro do arquivo (espaços e enter contam tambem)
 * utilizand um min heap a gente cria uma arvore com os valores dos nós e fazendo o caminho(para esquerda 0 e direitra 1)
 * geramos a arvore com os valores binarios correspondentes a cada caracter
 * 
 * 
 * *** utilizei de ajuda de vários fóruns na internet, então meu cósigo pode ter um pedacinho de cada haha
 */

package compactação_thayane;

/**
 * todas as bibliotecas necessarias
 * bibliotecas que nunca utilizei, como as que sao usadas para ler gerar e reescrever arquivos txt
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ra159049
 */
public class CompacDescompac {
    
    /**
     * criando construtures get e set de cada variavel
     */ 
    public class Huffman
{
    private char caracter;
    private long freq;
    private Huffman filhoDir, filhoEsq;
    private String cod;
    
    Huffman()
    {;}
    Huffman(char c,int f)
    {
        caracter=c;
        freq=f;
        filhoDir=filhoEsq=null;
    }
    
    public Huffman create(Huffman a,Huffman b)
    {
        this.freq=a.getfreq()+b.getfreq();
        
        this.caracter='-';
        this.filhoEsq=a;
        this.filhoDir=b;
        return this;
    }

    public char getcaracter() {
        return caracter;
    }

    public void setcaracter(char caracter) {
        this.caracter = caracter;
    }

    public long getfreq() {
        return freq;
    }

    public void setfreq(long freq) {
        this.freq = freq;
    }

    public Huffman getfilhoDir() {
        return filhoDir;
    }

    public void setfilhoDir(Huffman filhoDir) {
        this.filhoDir = filhoDir;
    }

    public Huffman getfilhoEsq() {
        return filhoEsq;
    }

    public void setfilhoEsq(Huffman filhoEsq) {
        this.filhoEsq = filhoEsq;
    }

    public String getcod() {
        return cod;
    }

    public void setcod(String cod) {
        this.cod = cod;
    }
    
}  
    
    
    
    /**
     * @param args the command line arguments
     */
    private static HashMap<Character,String> codifica;
    private static HashMap<String,Character> decodifica;
    public static int contaFreq[];
    public static int freqTotal = 0;//numero de caracteres no arquivo orignal
    public static int totalCodificado = 0;//numero de caracteres no arquivo codificado
    public static int totalDecodificado = 0;//numero de caracteres no arquivo decodificado
    /**
     * IMPORTANTE: nas seguintes variaveis globais definimos ONDE está o arquivo txt a ser codificado e decodificado
     * e tambem onde ele será salvo
     * Para o salvamento dos arquivos recomendo que coloque o diretorio da pasta do projeto
     */
    private final String arqv = "\\\\serv-acad-new\\home$\\ra159049\\Documents\\NetBeansProjects\\Compactação_thayane\\texto.txt";
    private final String salvaarqv = "\\\\serv-acad-new\\home$\\ra159049\\Documents\\NetBeansProjects\\Compactação_thayane";
    
    public static void main(String[] args) {
        // TODO code application logic here
        CompacDescompac huffcod = new CompacDescompac();
        
        huffcod.freq();
        Huffman raiz = huffcod.codifica();
        huffcod.generateKey();
        huffcod.geraArqvCodificado();
        huffcod.geraArqvDecodificado();
        huffcod.decodifica(raiz);
        huffcod.decodeBit(raiz);
        huffcod.quantidadebits();
    }
    
    /*
    esta função vai contar a frequencia dos caracteres, de acordo com a tabela ASCII
    */
    private void freq()
    {
        contaFreq = new int[256];        
        try(FileReader fr = new FileReader(arqv))
        {
            int c;           
            while((c = fr.read())!= -1)
                if(c<256)
                {
                    contaFreq[c]++;
                    freqTotal++;
                }            
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
    }
    /*
        aqui fazemos um min heap
    */
    private Huffman codifica()
    {
        int n = contaFreq.length;
        
        PriorityQueue <Huffman> minheap= new PriorityQueue<>(n,FREQUENCY_COMPARATOR);
        
        char c;
        int a;
        for(int i=0;i<n;i++)
        {
            if(contaFreq[i]!=0)
                minheap.add(new Huffman((char)i,contaFreq[i]));
        }
        
        Huffman z=null;
        while(minheap.size()>1)
        {
           
            Huffman x=minheap.peek();
            minheap.poll();
            Huffman y=minheap.peek();
            minheap.poll();
            x.setcod("0");
            y.setcod("1");
            z=new Huffman();
            z.setfreq(x.getfreq()+y.getfreq());
            z.setfilhoEsq(x);
            z.setfilhoDir(y);
            minheap.add(z);            
        }
        
        codifica = new HashMap<>();
        
        Huffman raiz=z;
        transform(raiz,"");
        
        System.out.println("");
        
        for(int i=0;i<n;i++)
           if(contaFreq[i]!=0)
            System.out.println((char)i+"\t"+contaFreq[i]+"\t"+codifica.get((char)i));
        return raiz;
    }
    /*
     checa o caracter com a menor frequencia
    */        
    private static final Comparator<Huffman> FREQUENCY_COMPARATOR = (Huffman o1, Huffman o2) -> (int) (o1.getfreq()-o2.getfreq());
    /*
     com  arecursão vamos gerar os binarios de cada caracter apartir da arvore de huffman 
    */
    private void transform(Huffman raiz,String s)throws NullPointerException
    {   
        if(raiz.getcod()!=null)
            s+=raiz.getcod();
        if(raiz.getfilhoEsq()==null && raiz.getfilhoDir()==null )
            {
                
                codifica.put(raiz.getcaracter(), s);
                return;
            }
        
         transform(raiz.getfilhoEsq(), s);
         transform(raiz.getfilhoDir(), s);
    }
    /*
     essa função gera uma chave e um arquivo em csv que vai conter exatamente os valores das letras em binario
     achei a funcção na internet e achei interessante colocar ela no codigo
    */
    private void generateKey()
    {
        Set<Map.Entry<Character,String>> set = codifica.entrySet();
        StringBuffer val=new StringBuffer();
        for(Map.Entry<Character,String> me: set)
        {          
            val.append(getEspaEnter(me.getKey())).append(",").append(me.getValue()).append("\n");
        }
        
        try(FileWriter fw = new FileWriter(salvaarqv+"\\key.csv"))
        {
            fw.write(val.toString());
        } catch (IOException ex) {
            Logger.getLogger(CompacDescompac.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    converte os caracteres de esaço ou enter caso tenham
    */
    private static String getEspaEnter(char h)
    {
        switch(h)
        {
            case '\n': return "\\n";
            case '\t': return "\\t";
            //case " ": return "\\u32";
            default: return Character.toString(h);
        }
        
    }
    /*
    nessafunção vai gerar o arquivo codificado
    */
    private void geraArqvCodificado()
    {
        StringBuffer val=new StringBuffer();
        
        try(FileReader fr = new FileReader(arqv);)
        {
            int c;
            while((c = fr.read())!= -1)
                if(c<256)
                    val.append(codifica.get((char)c));  
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        
        try(FileWriter fw = new FileWriter(salvaarqv+"\\codificado.txt");)
        {
            fw.write(val.toString());
        } catch (IOException ex) {
            Logger.getLogger(CompacDescompac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
    /*
     função qu vai decodificar o codigo de huffman ja criado (e salvo num arquvio) e vai salvar em um arquivo com o nome decodificado
    */
    private void decodifica(Huffman raiz)
    {
        Huffman hraiz = raiz;
        StringBuffer val = new StringBuffer();
        try(FileReader fr = new FileReader(salvaarqv+"\\codificado.txt");)
        {
            int c;
            while((c = fr.read())!= -1)
                if(c<256)
                {
                    val.append((char)c);
                }   
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        StringBuffer output = new StringBuffer();
        for(int i=0;i<val.length();i++)
        {
            char ch = val.charAt(i);
            if(ch=='0')
                raiz =  raiz.getfilhoEsq();
            else if(ch=='1')
                raiz = raiz.getfilhoDir();
            if(raiz.getfilhoDir()==null && raiz.getfilhoEsq()==null)
            {
                
                output.append(raiz.getcaracter());
                raiz=hraiz;
            }
        }
        totalDecodificado = output.length();
        try(FileWriter fw = new FileWriter(salvaarqv+"\\decodificado.txt");)
        {
            fw.write(output.toString());
        } catch (IOException ex) {
            Logger.getLogger(CompacDescompac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /*
     função que com os arquivos criados de bits conta os bits de cada um deles
     com tamanho
    */
    private void quantidadebits()
    {
        System.out.println("\nNúmero total de bits do arquivo original= "+freqTotal*8);
        for(int i=0;i<contaFreq.length;i++)
           if(contaFreq[i]!=0)
           {
               
               totalCodificado+=contaFreq[i]*codifica.get((char)i).length();
           }
               
        System.out.println("\nNúmero total de bits no arquivo codificado= "+totalCodificado);
        System.out.println("\nNúmero total de bits no arquivo decodificado= "+totalDecodificado*8);
    }
    
    private void geraArqvDecodificado()
    {
        System.out.println("Valores binario letra a letra:");
        ArrayList<Byte> contaByte = new ArrayList<>();  
        StringBuffer val=new StringBuffer();
        
        
        try(FileInputStream fis = new FileInputStream(arqv);)
        {
            int c;
            while((c = fis.read())!= -1)
                if(c<256)
                {
                    
                    val.append(codifica.get((char)c));  
                    System.out.print(codifica.get((char)c)+",");
                    
                    
                }
               
               
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        
        try
        {
            byte b=0;
            System.out.println("\n\njuntos e shallow now: "+val);
            int len;
            for(len=val.length();len<val.length()+8;len++)
                if(len%8==0)break;
             
            
           for(int i=0;i<len;i++)
            {
                if(i>=val.length())
                    b = (byte)(b<<1);
                else
                {
                    b = (byte)((b<<1)|Character.getNumericValue(val.charAt(i)));
                }
         
                if(((i+1)%8==0&&i!=0)||i==len-1)
                    {
                        
                        contaByte.add((byte)b);
                        b=0;
                    }
                
            }
        }
        catch(NullPointerException ne){;}
        //for(int i=0;i<n;i++)
        //System.out.println(val.length()/8);
        //System.out.println("\n:"+contaByte);  
        try(FileOutputStream fos = new FileOutputStream(salvaarqv+"\\bitscodificados.txt");)
        {
            for(byte b:contaByte)
            {
                
                fos.write(b);
            }
        } catch (IOException ex) {
            Logger.getLogger(CompacDescompac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void decodeBit(Huffman raiz)
    {
        Huffman hraiz = raiz;
        ArrayList<Byte> contaByte = new ArrayList<>();  
        try(FileInputStream fis = new FileInputStream(salvaarqv+"\\bitscodificados.txt");)
        {
            byte b;
            while((b = (byte)fis.read())!= -1)
                
                {
                    
                    contaByte.add(b);
                }   
        }
        catch(IOException e)
        {
            System.out.println("IO error :"+e);
        }
        //System.out.println("checar contaByte:"+contaByte);
        /**
         * String builder e string buffer
         * usamos pq não se pode concatenar (de maneira correta) string pois ela são imutaveis 
         */
        StringBuilder output = new StringBuilder();
        StringBuffer val = new StringBuffer();
        for(byte b:contaByte)
        {
            byte cb=b;
            val.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            
        }
        System.out.println("\nCodificado com o complemento de bits a direita= "+val);
        for(int i=0;i<val.length();i++)
        {
            char ch = val.charAt(i);
            if(ch=='0')
                raiz =  raiz.getfilhoEsq();
            else if(ch=='1')
                raiz = raiz.getfilhoDir();
            if(raiz.getfilhoDir()==null && raiz.getfilhoEsq()==null)
            {
                //System.out.print(raiz.getCharacter());
                output.append(raiz.getcaracter());
                raiz=hraiz;
            }
        }
        totalDecodificado = output.length();
        try(FileWriter fw = new FileWriter(salvaarqv+"\\bitdecodificado.txt");)
        {
            fw.write(output.toString());
        } catch (IOException ex) {
            Logger.getLogger(CompacDescompac.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
