import java.util.ArrayList;

public class Literal
{
   boolean not;
   String name;
   ArrayList<String> args;
  
   public Literal() {
      this.not = false;
      this.name = "";
      this.args = new ArrayList<String>();
   }
   
   public Literal(String name, boolean not, ArrayList<String> args) {
      this.not = not;
      this.name = name;
      this.args = new ArrayList<String>(args);
   }
   
   public String toString() {
      String lit = "";
      if(not)
         lit += "~";
      lit += (name + "(");
      
      for(int i = 0; i < args.size(); i++) {
         if(i+1 != args.size())
            lit += (args.get(i) + ",");
         else
            lit += args.get(i);
         
      }
      lit += ")";
      
      return lit;
   }
   
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      
      if (!Literal.class.isAssignableFrom(obj.getClass())) {
         return false;
      }
      
      final Literal other = (Literal) obj;
      
      if (!this.name.equals(other.name)) {
         return false;
      }
      
      if(this.not != other.not) {
         return false;
      }
      
      if(this.args.size() != other.args.size()) {
         return false;
      }
      
      for(int i = 0; i < this.args.size(); i++) {
         if(!this.args.get(i).equals(other.args.get(i))) {
            return false;
         }
      }
      
      return true;
   }

   @Override
   public int hashCode() {
      int hash = name.hashCode();
      for(int i = 0; i < args.size(); i++) {
         hash += args.get(i).hashCode() * (i+1);
      }
      if(not) 
         hash += 1000;
      return hash;
   }
   
   public Literal not() {
      return new Literal(this.name, !this.not, this.args);
   }
}