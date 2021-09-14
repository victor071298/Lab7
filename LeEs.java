class LeEs {
    private int leit, escr;  
    
    // Construtor da classe
    LeEs() { 
       this.leit = 0; //leitores lendo
       this.escr = 0; //escritor escrevendo (máx 1)
    } 
    
    // Entrada para leitores
    public synchronized void EntraLeitor (int id,String type) {
      try { 
        while (this.escr > 0) {
           System.out.println (type+ " " +id+ " bloqueado");
           wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.leit++;  //registra que ha mais um leitor lendo
        System.out.println (type+ " " +id+ " lendo");
      } catch (InterruptedException e) { }
    }
    
    // Saida para leitores
    public synchronized void SaiLeitor (int id,String type) {
       this.leit--; //registra que um leitor saiu
       if (this.leit == 0) 
             this.notify(); //libera escritor (caso exista escritor bloqueado)
       System.out.println (type+ " " +id+ " saindo");
    }
    
    // Entrada para escritores
    public synchronized void EntraEscritor (int id, String type) {
      try { 
        while ((this.leit > 0) || (this.escr > 0)) {
           System.out.println (type+ " " +id+ " bloqueado");
           wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.escr++; //registra que ha um escritor escrevendo
        System.out.println (type+ " " +id+ " escrevendo");
      } catch (InterruptedException e) { }
    }
    
    // Saida para escritores
    public synchronized void SaiEscritor (int id , String type) {
       this.escr--; //registra que o escritor saiu
       notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
       System.out.println (type+ " " +id+ " saindo");
    }
  }

// Leitor
class Read extends Thread {
  int id; //Identificador da thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das threads
  String type = new String("Leitor"); //Identificador do tipo de thread (leitor, escritor ou leitor escritor)
  // Construtor
  Read (int id, int delayTime, LeEs m) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
  }

  // Método que determina primalidade de um inteiro n
  private boolean Prime(int n){  
    if(n==1||n==0){return false;}
    for(int i=2;i<=n/2;i++){
      if(n%i==0){
        return false;
      }
    }
    return true;
  }


  // Método executado pela thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id,type);
        System.out.println("Leitor " +this.id+ " leu " +Monitor.v);
        if(Prime(Monitor.v)){
         System.out.println("Leitor " +this.id+ " viu que " +Monitor.v+ " e primo");
        }
        else{
          System.out.println("Leitor " +this.id+ " viu que " +Monitor.v+ " nao e primo");
        }
        this.monitor.SaiLeitor(this.id,type);
        sleep(this.delay); 
      }
    } catch (InterruptedException e) { return; }
  }
}

// Escritor
class Write extends Thread {
  int id; //Identificador da thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das threads
  String type = new String("Escritor"); //Identificador do tipo de thread (leitor, escritor ou leitor escritor)

  // Construtor
  Write (int id, int delayTime, LeEs monitor) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = monitor;
  }

  // Método executado pela thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraEscritor(this.id,type); 
        Monitor.v = this.id;
        System.out.println("Escritor " +this.id+ " escreveu " + Monitor.v);
        this.monitor.SaiEscritor(this.id,type); 
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

//LeitorEscritor
class Writeread extends Thread{
  
  int id; //Identificador da thread
  int delay; //Atraso
  LeEs monitor; //Objeto monitor para coordenar a lógica de execução das threads
  String type = new String("Leitor e Escritor"); //Identificador do tipo de thread (leitor, escritor ou leitor escritor)

  // Construtor
  Writeread (int id, int delayTime, LeEs monitor) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = monitor;
  }

  // Método para determinar se um inteiro n é par ou impar
  private void parImpar(int n){
    if(n%2==0){
      System.out.println("Leitor e Escritor viu que " +n+ " e Par ");
    }
    else{
      System.out.println("Leitor e Escritor viu que " +n+ " e Impar");
    }

  }
  // Método executado pela thread
  public void run () {
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id,type);
        System.out.println("Leitor e Escritor " +this.id+ " Leu " +Monitor.v);
        parImpar(Monitor.v);
        this.monitor.SaiLeitor(this.id,type);
        this.monitor.EntraEscritor(this.id,type);
        Monitor.v=2*Monitor.v; 
        System.out.println("Leitor e Escritor " +this.id+ " escreveu " +Monitor.v );
        this.monitor.SaiEscritor(this.id,type); 
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

class Monitor {
  public static int v = 0; // Variável global
  static final int R = 5; // Quantidade de leitores
  static final int W = 5; // Quantidade de escritores
  static final int WR = 2; // Quantidade de leitores e escritores
  public static void main(String[] args) {
    int i;
    LeEs monitor = new LeEs();            // Monitor
    Read[] r = new Read[R];       // Threads leitores
    Write[] w = new Write[W];   // Threads escritores
    Writeread[] wr = new Writeread[WR]; // Threads leitoras/escritoras
    for (i=0; i<R; i++) {
      System.out.println("Leitor " +(i+1)+ " criado");
      r[i] = new Read(i+1, (i+1)*500, monitor); 
      r[i].start(); 
    }
   for (i=0; i<W; i++) {
      System.out.println("Escritor " +(i+1)+ " criado");
      w[i] = new Write(i+1, (i+1)*500, monitor); 
      w[i].start(); 
    }
   for (i=0; i<WR; i++) {
    System.out.println("Leitor/Escritor " +(i+1)+ " criado");
    wr[i] = new Writeread(i+1, (i+1)*500, monitor); 
    wr[i].start(); 
    }
  }
}
