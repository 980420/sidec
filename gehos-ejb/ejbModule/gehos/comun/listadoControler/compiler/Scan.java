/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gehos.comun.listadoControler.compiler;

/**
 *
 * @author Gerardo
 */
public class Scan {
    private String base;
    private int pos;

    public Scan(String base){
        this.base = base;
        letters = base.toCharArray();
        pos = 0;
    }

    private char[] letters;

    public Token next() throws Exception{
        while(pos < letters.length && 
                (letters[pos] == ' ' || letters[pos] == '\n' || letters[pos] == '\r'))
            pos++;
        if(pos == letters.length){
            Token a = new Token();
            a.setType(TokenType.Null);
            a.setValue("");
            return a;
        }
        String tok = Character.toString(letters[pos]);
        if(letters[pos] == '\''){
            pos++;
            tok="";
            while(pos < letters.length && letters[pos] != '\''){
                tok += Character.toString(letters[pos]);
                pos++;
            }
            pos++;
            Token t = new Token();
            t.setType(TokenType.Constant);
            t.setValue(tok);
            return t;
        }
        if(tok.matches("[0-9]+")){
            pos++;
            while(pos < letters.length && Character.toString(letters[pos]).matches("[0-9]+")){
                tok += Character.toString(letters[pos]);
                pos++;
            }
            Token t = new Token();
            t.setType(TokenType.Constant);
            t.setValue(tok);
            return t;
        }
        if(tok.matches("[a-zA-Z_]+")){
            pos++;
            while(pos < letters.length && Character.toString(letters[pos]).matches("[a-zA-Z0-9]+")){
                tok += Character.toString(letters[pos]);
                pos++;
            }
            Token t = new Token();
            t.setType(TokenType.Variable);
            t.setValue(tok);
            if(tok.equals("lower")){
                t.setType(TokenType.Function);
            }
            if(tok.equals("concat")){
                t.setType(TokenType.Function);
            }
            if(tok.equals("current_date")){
                t.setType(TokenType.Function);
            }
            if(tok.equals("and")){
                t.setType(TokenType.And);
            }
            if(tok.equals("or")){
                t.setType(TokenType.Or);
            }
            if(tok.equals("like")){
                t.setType(TokenType.Operator);
            }
            return t;
        }
        Token t = new Token();
        switch(letters[pos]){
            case '(':
                t.setType(TokenType.OpenPar);
                t.setValue("(");
                break;
            case ')':
                t.setType(TokenType.ClosePar);
                t.setValue(")");
                break;
            case '.':
                t.setType(TokenType.Point);
                t.setValue(".");
                break;
            case '}':
                t.setType(TokenType.CloseEl);
                t.setValue("}");
                break;
            case ',':
                t.setType(TokenType.ComSep);
                t.setValue(",");
                break;
            case '#':
                if(letters[pos + 1] != '{')
                    throw new Exception("Expected '{'");
                pos++;
                t.setValue("#{");
                t.setType(TokenType.OpenEl);
                break;
            case '>':
                if(letters[pos + 1] == '='){
                    t.setValue(">=");
                    pos++;
                }
                else{
                    t.setValue(">");
                }
                t.setType(TokenType.Operator);
                break;
            case '<':
                if(letters[pos + 1] == '='){
                    t.setValue("<=");
                    pos++;
                }
                else if(letters[pos + 1] == '>'){
                    t.setValue("<>");
                    pos++;
                }
                else{
                    t.setValue("<");
                }
                t.setType(TokenType.Operator);
                break;
            case '=':
                t.setType(TokenType.Operator);
                t.setValue("=");
                break;
            case '!':
                if(letters[pos + 1] != '=')
                    throw new Exception("Expected '='");
                t.setType(TokenType.Operator);
                t.setValue("!=");
                break;
            default:
                throw new Exception("Ilegal character");
        }
        pos++;
        if(t.getValue() == null){
            t.setValue(" ");
        }
        return t;
    }

    public void before(Token token){
        pos -= token.getValue().length();
    }

    public String getRest(){
        return base.substring(pos);
    }
}
