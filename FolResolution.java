import java.util.*;
import java.io.*;
import java.util.regex.*;

public class FolResolution 
{
   static int q;
   static int s;
   static PrintWriter out;
   static Literal [] queries;
   static ArrayList<HashSet<Literal>> kbMaster;
   static long time1;

   public static void main(String[]args) throws FileNotFoundException {
      time1 = System.nanoTime();
      ReadInput();
      //PrintKB(kbMaster);
      for(int i = 0; i < queries.length; i++) {
         System.out.println(RunResolution(queries[i]));
         if(RunResolution(queries[i]))
            out.println("TRUE");
         else
            out.println("FALSE");
      }
      out.close();
   }
   
   static boolean RunResolution(Literal query) {
      ArrayList<HashSet<Literal>> kb = new ArrayList<HashSet<Literal>>(kbMaster);
      HashSet<Literal> qClause = new HashSet<Literal>();
      qClause.add(new Literal(query.name, !query.not, query.args));
      kb.add(qClause);
     
      while(true) {
         long time2 = System.nanoTime();
         if(time2 - time1 >= 2000000000l) {return false;}
         ArrayList<HashSet<Literal>> newClauses = new ArrayList<HashSet<Literal>>();
         for(int i = 0; i < kb.size() - 1; i++) {
            for(int j = i+1; j < kb.size(); j++) {
            
               ArrayList<HashSet<Literal>> resolvents = Resolve(kb.get(i), kb.get(j));
               if(resolvents == null) {return false;}
               if(resolvents.contains(new HashSet<Literal>())) {
                  return true;
               }
               
               for(HashSet<Literal> clause : resolvents) {
                  if(!newClauses.contains(new HashSet<Literal>(clause))) {
                     //System.out.println(clause);
                     newClauses.add(clause);
                  }
               }
               
            }
         }
         
         boolean cont = false;
         for(HashSet<Literal> clause : newClauses) {
            if(!kb.contains(new HashSet<Literal>(clause))) {
               //System.out.println(clause);
               kb.add(clause);
               cont = true;
            }
         }
         
         if(!cont) {return false;}
      }
   }

   static ArrayList<HashSet<Literal>> Resolve (HashSet<Literal> c1, HashSet<Literal> c2) {
      ArrayList<HashSet<Literal>> resolvents = new ArrayList<HashSet<Literal>>();
      
      for(Literal t1 : c1) {
         for(Literal t2 : c2) {
            long time2 = System.nanoTime();
            if(time2 - time1 >= 20000000000l) {
               return null;	
            }

            if(t1.name.equals(t2.name) && !t1.not == t2.not && 
               (((hasVar(t1) && hasConst(t2)) || (hasVar(t2) && hasConst(t1))) ||
                  (hasConst(t1) && hasConst(t2)))) {
               
               HashMap<String,String> sub = Unify(t1.args,t2.args,new HashMap<String,String>());
               if(sub == null)
                  break;
              
               HashSet<Literal> resolvent = new HashSet<Literal>();
               for(Literal lit : c1) {
                  resolvent.add(new Literal(lit.name, lit.not, lit.args));
               }
               for(Literal lit : c2) {
                  resolvent.add(new Literal(lit.name, lit.not, lit.args));
               }
               resolvent.remove(t1); resolvent.remove(t2);
               
               //System.out.print(c1); System.out.print(c2); System.out.print(resolvent); System.out.print(sub);
               
               //Apply substitution
               
               ArrayList<Literal> replacements = new ArrayList<Literal>();
               for(Literal term : resolvent) {
                  for(int i = 0; i < term.args.size(); i++) {
                     if(sub.containsKey(term.args.get(i))) {
                        term.args.set(i,sub.get(term.args.get(i)));
                        replacements.add(term);
                     }
                  }
               }
               
               for(Literal term : replacements) {
                  resolvent.remove(term);
               }
               
               //System.out.print(resolvent); System.out.println();
               
               if(!resolvents.contains(new HashSet<Literal>(resolvent))) {
                  resolvents.add(resolvent);
               }
               break;
            }
         }
      }
      
      return resolvents;
   }
   
   static HashMap<String,String> Unify(ArrayList<String> xList, ArrayList<String> yList, 
                                       HashMap<String,String> sub) {
                                       
      long time2 = System.nanoTime();
      if(time2 - time1 >= 20000000000l) {
         return null;	
      } 
                                     
      if(sub == null) {
         return null;
      }
      
      if(xList.size() == 1 && yList.size() == 1) {
         String x = xList.get(0); String y = yList.get(0);
         
         if(x.equals(y)) {
            return sub;
         }
         
         else if(isVar(x)) {
            return UnifyVar(x,y,sub);
         }
         
         else if(isVar(y)) {
            return UnifyVar(y,x,sub);
         }
         
      }
      
      else if(xList.size() != 0 && yList.size() != 0) {
         ArrayList<String> xRest = new ArrayList<String>(xList.subList(1,xList.size()));
         ArrayList<String> yRest = new ArrayList<String>(yList.subList(1,yList.size()));
         ArrayList<String> xFirst = new ArrayList<String>(xList.subList(0,1));
         ArrayList<String> yFirst = new ArrayList<String>(yList.subList(0,1));
         return Unify(xRest,yRest,Unify(xFirst,yFirst,Unify(xFirst,yFirst,sub)));
      }
      
      return null;
      
   }
   
   static HashMap<String,String> UnifyVar(String var, String x, HashMap<String,String> sub) {
      long time2 = System.nanoTime();
      if(time2 - time1 >= 20000000000l) {
         return null;	
      }
      
      if(sub.containsKey(var)) {
         ArrayList<String> valRet = new ArrayList<String>();
         valRet.add(sub.get(var));
         
         ArrayList<String> xRet = new ArrayList<String>();
         xRet.add(x);
         
         return Unify(valRet,xRet,sub);
      }
      
      else if(sub.containsKey(x)) {
         ArrayList<String> valRet = new ArrayList<String>();
         valRet.add(sub.get(x));
         
         ArrayList<String> varRet = new ArrayList<String>();
         varRet.add(var);
         
         return Unify(varRet,valRet,sub);
      }
      
      else {
         sub.put(var,x);
         return sub;
      }
   }
      
   static void ReadInput() throws FileNotFoundException {
      File input = new File("input.txt");
      Scanner in = new Scanner(input);
      out = new PrintWriter("output.txt");
      kbMaster = new ArrayList<HashSet<Literal>>();
      
      q = in.nextInt(); queries = new Literal [q];
      in.nextLine();
      for(int i = 0; i < q; i++) {
      
         String line = in.nextLine();
         Literal next = CreateLiteral(line);        
         queries[i] = next;
      }
      
      s = in.nextInt(); in.nextLine();
      for(int i = 0; i < s; i++) {
         HashSet<Literal> clause = new HashSet<Literal>();
         String line = in.nextLine();
         Scanner inLine = new Scanner(line);
         
         while(inLine.hasNext()) {
            String next = inLine.next();
            if(!next.equals("|")) {
               Literal lit = CreateLiteral(next);
               clause.add(lit);
            }
         }
         kbMaster.add(clause);
         inLine.close();
      }
      in.close();
   }
   
   static Literal CreateLiteral(String line) {
      Literal next = new Literal();
      int j = 0;
      int bIndex = line.indexOf("(");
      int eIndex = line.indexOf(")");

      if(line.charAt(0) == '~') {
         j++;
         next.not = true;
      }

      String name = "";
      while(j != bIndex) {
         name += line.charAt(j);
         j++;
      }

      next.name = name;
      j++;

      while(j <= eIndex) {
         String arg = "";
         while(line.charAt(j) != ',' && j != eIndex) {
            arg += line.charAt(j);
            j++;
         }

         next.args.add(arg);
         j++;
      } 
      return next;  
   }
   
   static void PrintKB (ArrayList<HashSet<Literal>> kb) {
      for(int i = 0; i < kb.size(); i++) {
         Iterator<Literal> it = kb.get(i).iterator();
            while(it.hasNext()){
               Literal next = it.next();
               if(it.hasNext())
                  System.out.print(next + " | ");
                else
                  System.out.print(next);
            }
         if(i != kb.size() - 1)
            System.out.println();
      }
   }
   
   static boolean isVar(String str) {
      String regex = "[a-z]+";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      return matcher.matches();
   }
   
   static boolean hasVar (Literal lit) {
      for(String str : lit.args) {
         if (isVar(str))
            return true;
      }
      
      return false;
   }
   
   static boolean hasConst (Literal lit) {
      for(String str : lit.args) {
         if (!isVar(str))
            return true;
      }
      
      return false;
   }
   
}