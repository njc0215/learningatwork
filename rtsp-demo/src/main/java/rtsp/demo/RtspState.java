package rtsp.demo;


public enum RtspState 
{
     INIT ("INIT"),
     READY ("READY"),
     PLAYING ("PLAYING");
     
     private String desc;
     
     RtspState(String desc){
    	 this.desc = desc;
     }
     
     public String toString(){
    	 return desc;
     }
}