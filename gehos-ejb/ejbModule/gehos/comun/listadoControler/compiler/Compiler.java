/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.compiler;

import gehos.comun.listadoControler.parameters.ConcatParameter;
import gehos.comun.listadoControler.parameters.ConstantParameter;
import gehos.comun.listadoControler.parameters.CurrentDateParameter;
import gehos.comun.listadoControler.parameters.ExpressionParameter;
import gehos.comun.listadoControler.parameters.LowerParameter;
import gehos.comun.listadoControler.parameters.ObjectParameter;
import gehos.comun.listadoControler.parameters.Parameter;
import gehos.comun.listadoControler.restrictions.AndRestriction;
import gehos.comun.listadoControler.restrictions.DistintRestriction;
import gehos.comun.listadoControler.restrictions.EqualsRestrinction;
import gehos.comun.listadoControler.restrictions.GeneralRestriction;
import gehos.comun.listadoControler.restrictions.LikeRestriction;
import gehos.comun.listadoControler.restrictions.MayorEqualsRestriction;
import gehos.comun.listadoControler.restrictions.MayorRestriction;
import gehos.comun.listadoControler.restrictions.MinorEqualsRestriction;
import gehos.comun.listadoControler.restrictions.MinorRestrinction;
import gehos.comun.listadoControler.restrictions.OrRestriction;
import gehos.comun.listadoControler.restrictions.Restriction;

/**
 *
 * @author Gerardo
 */
public class Compiler {

    public static GeneralRestriction getRestriction(String restric)
        throws Exception{
        GeneralRestriction gen = new GeneralRestriction();
        gen.setBaseRestriction(getRestriction2(restric));
        String exp = "";
        char[] chars = restric.toCharArray();
        int state = 0;
        for (char c : chars) {
            switch(state){
                case 0:
                    if(c == '#'){
                        state = 1;
                    }
                    break;
                case 1:
                    if(c == '{'){
                        state = 2;
                        exp = "#{";
                    }
                    else{
                        throw new Exception("Not valid expression");
                    }
                    break;
                case 2:
                    if(c == '}'){
                        state = 3;
                    }
                    exp += c;
                    break;
                case 3:
                    if(c == '#'){
                        throw new Exception("More than one expression");
                    }
                    break;
            }
        }
        Parameter p = new ExpressionParameter();
        p.setParameter(exp);
        gen.setExpressionParameter(p);
        return gen;
    }


    private static Restriction getRestriction2(String restric)
        throws Exception{
        Restriction r1 = null;
        Scan scan = new Scan(restric);
        Token t = scan.next();
        if(t.getType().equals(TokenType.OpenPar)){
            r1 = getParRestriction(scan);
            t = scan.next();
             if(t.getType().equals(TokenType.Null))
                return r1;
            if(t.getType().equals(TokenType.And)){
                Restriction r2 = getRestriction2(scan.getRest());
                AndRestriction result = new AndRestriction();
                result.setLeft(r1);
                result.setRigth(r2);
                return result;
            }
            if(t.getType().equals(TokenType.Or)){
                Restriction r2 = getRestriction2(scan.getRest());
                OrRestriction result = new OrRestriction();
                result.setLeft(r1);
                result.setRight(r2);
                return result;
            }
        }
        scan.before(t);
        Parameter l = getParameter(scan);
        t = scan.next();
        if(!t.getType().equals(TokenType.Operator))
             throw new Exception("Expected operator");
        String op = t.getValue();
        Parameter r = getParameter(scan);
        r1 = getOperatorRestriction(op, l, r);
        t = scan.next();
        if(t.getType().equals(TokenType.Null))
            return r1;
        if(t.getType().equals(TokenType.And)){
            Restriction r2 = getRestriction2(scan.getRest());
            AndRestriction result = new AndRestriction();
            result.setLeft(r1);
            result.setRigth(r2);
            return result;
        }
        if(t.getType().equals(TokenType.Or)){
            Restriction r2 = getRestriction2(scan.getRest());
            OrRestriction result = new OrRestriction();
            result.setLeft(r1);
            result.setRight(r2);
            return result;
        }
        throw new Exception("Expected and/or reserved word");
    }

    private static Parameter getParameter(Scan scan) throws Exception{
        Token t = scan.next();
        if(t.getType().equals(TokenType.Constant)){
            ConstantParameter p = new ConstantParameter();
            p.setParameter(t.getValue());
            return p;
        }
        if(t.getType().equals(TokenType.OpenEl)){
            String ex = "#{";
            t = scan.next();
            while(!t.getType().equals(TokenType.CloseEl)){
                if(!t.getType().equals(TokenType.Variable) &&
                       !t.getType().equals(TokenType.Point)){
                    throw new Exception("Ilegal sintax for EL");
                }
                ex += t.getValue();
                t = scan.next();
            }
            ex += "}";
            ExpressionParameter p = new ExpressionParameter();
            p.setParameter(ex);
            return p;
        }
        if(t.getType().equals(TokenType.Function)){
            if(t.getValue().equals("current_date"))
                return new CurrentDateParameter();
            else if(t.getValue().equals("lower")){
                t = scan.next();
                if(!t.getType().equals(TokenType.OpenPar)){
                    throw new Exception("Expected '('");
                }
                Parameter r = getParameter(scan);
                t = scan.next();
                if(!t.getType().equals(TokenType.ClosePar)){
                    throw new Exception("Expected ')'");
                }
                LowerParameter p = new LowerParameter();
                p.setRight(r);
                return p;
            }
            else if(t.getValue().equals("concat")){
                t = scan.next();
                if(!t.getType().equals(TokenType.OpenPar)){
                    throw new Exception("Expected '('");
                }
                Parameter l = getParameter(scan);
                t = scan.next();
                if(!t.getType().equals(TokenType.ComSep)){
                    throw new Exception("Expected ','");
                }
                Parameter r = getParameter(scan);
                t = scan.next();
                if(!t.getType().equals(TokenType.ClosePar)){
                    throw new Exception("Expected ')'");
                }
                ConcatParameter p = new ConcatParameter();
                p.setLeft(l);
                p.setRight(r);
                return p;
            }
        }
        if(t.getType().equals(TokenType.OpenPar)){
            return getParParameter(scan);
        }
        if(t.getType().equals(TokenType.Variable)){
            String var = t.getValue();
            t = scan.next();
            while(t.getType().equals(TokenType.Point)){
                var += ".";
                t = scan.next();
                if(!t.getType().equals(TokenType.Variable)){
                    throw new Exception("Variable expected");
                }
                var += t.getValue();
                t = scan.next();
            }
            scan.before(t);
            ObjectParameter p = new ObjectParameter();
            p.setParameter(var);
            return p;
        }
        throw new Exception("Paramenter expected");
    }

    private static Parameter getParParameter(Scan scan) throws Exception{
        int cant = 1;
        String rest = "";
        while(cant > 0){
            Token t = scan.next();
            if(!t.getType().equals(TokenType.ClosePar) || cant != 1){
                rest += t.getValue();
                if(t.getType().equals(TokenType.ClosePar) ||
                        t.getType().equals(TokenType.OpenPar)
                        || t.getType().equals(TokenType.And)
                        || t.getType().equals(TokenType.CloseEl)
                        || t.getType().equals(TokenType.Constant)
                        || t.getType().equals(TokenType.Operator)
                        || t.getType().equals(TokenType.Variable)
                        || t.getType().equals(TokenType.Or)){
                    rest += " ";
                }
            }
            if(t.getType().equals(TokenType.ClosePar)){
                cant--;
            }
            if(t.getType().equals(TokenType.OpenPar)){
                cant++;
            }
        }
        Scan s = new Scan(rest);
        return getParParameter(s);
    }

    private static Restriction getParRestriction(Scan scan) throws Exception{
        int cant = 1;
        String rest = "";
        while(cant > 0){
            Token t = scan.next();
            if(t.getType().equals(TokenType.Null)){
                throw new Exception("Bad parent expresion");
            }
            if(!t.getType().equals(TokenType.ClosePar) || cant != 1){
                if(!t.getType().equals(TokenType.Constant)
                        || t.getValue().matches("[0-9]+"))
                    rest += t.getValue();
                else
                    rest += "'" + t.getValue() + "'";
                if(t.getType().equals(TokenType.ClosePar) ||
                        t.getType().equals(TokenType.OpenPar)
                        || t.getType().equals(TokenType.And)
                        || t.getType().equals(TokenType.CloseEl)
                        || t.getType().equals(TokenType.Constant)
                        || t.getType().equals(TokenType.Operator)
                        || t.getType().equals(TokenType.Variable)
                        || t.getType().equals(TokenType.Or)){
                    rest += " ";
                }
            }
            if(t.getType().equals(TokenType.ClosePar)){
                cant--;
            }
            if(t.getType().equals(TokenType.OpenPar)){
                cant++;
            }
        }
        return getRestriction2(rest);
    }
    
    private static Restriction getOperatorRestriction(String op, Parameter l, Parameter r){
        if(op.equals(">")){
            MayorRestriction rest = new MayorRestriction();
            rest.setLeft(l);
            rest.setRight(r);
            return rest;
        }
        else if(op.equals("<")){
            MinorRestrinction rest = new MinorRestrinction();
            rest.setLeft(l);
            rest.setRight(r);
            return rest;
        }
        else if(op.equals(">=")){
            MayorEqualsRestriction rest = new MayorEqualsRestriction();
            rest.setLeft(l);
            rest.setRight(r);
            return rest;
        }
        else if(op.equals("<=")){
            MinorEqualsRestriction rest = new MinorEqualsRestriction();
            rest.setLeft(l);
            rest.setRight(r);
            return rest;
        }
        else if(op.equals("=")){
            EqualsRestrinction rest = new EqualsRestrinction();
            rest.setLeft(l);
            rest.setRigth(r);
            return rest;
        }
        else if(op.equals("<>") || op.equals("!=")){
            DistintRestriction rest = new DistintRestriction();
            rest.setLeft(l);
            rest.setRigth(r);
            return rest;
        }
        else if(op.equals("like")){
            LikeRestriction rest = new LikeRestriction();
            rest.setLeft(l);
            rest.setRight(r);
            return rest;
        }
        return null;
    }

}
