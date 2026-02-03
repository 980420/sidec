package gehos.ensayo.ensayo_disenno.session.reglas.dragndropexample;
/*package gehos.ensayo.ensayo_estadisticas.session.reglas.dragndropexample;

import gehos.ensayo.ensayo_estadisticas.session.reglas.Framework.Family;

import java.util.Collection;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@Name("dragDropBean")
@Scope(ScopeType.CONVERSATION)
public class DragDropExample 
{	
	static Family cf = Family.cf;
	static Family dotNet = Family.dotNet;
	static Family php = Family.php;
	
	private static final long serialVersionUID = 1416925735640720492L;
    private static final FrameworkFamilyPredicate CF_PREDICATE = new FrameworkFamilyPredicate(cf);
    private static final FrameworkFamilyPredicate DOT_NET_PREDICATE = new FrameworkFamilyPredicate(dotNet);
    private static final FrameworkFamilyPredicate PHP_PREDICATE = new FrameworkFamilyPredicate(php);
 
    private static final class FrameworkFamilyPredicate implements Predicate<Framework> {
        private Framework.Family family;
 
        public FrameworkFamilyPredicate(Family family) {
            super();
            this.family = family;
        }
 
        public boolean apply(Framework input) {
            return family.equals(input.getFamily());
        }
    }
 
    private List<Framework> source;
    private List<Framework> target; 
   
    @Create
    @Begin(join=true)
	public void init()
	{
		initList();
	}
 
    public Collection<Framework> getSource() {
        return source;
    }
 
    public Collection<Framework> getTarget() {
        return target;
    }
 
    public List<Framework> getTargetPHP() {
        return Lists.newLinkedList(Collections2.filter(target, PHP_PREDICATE));
    }
 
    public List<Framework> getTargetDotNet() {
        return Lists.newLinkedList(Collections2.filter(target, DOT_NET_PREDICATE));
    }
 
    public List<Framework> getTargetCF() {
        return Lists.newLinkedList(Collections2.filter(target, CF_PREDICATE));
    }
 
    public void moveFramework(Framework framework) {
        source.remove(framework);
        target.add(framework);
    }
 
    public void reset() {
        initList();
    }
 
    private void initList() {
        source = Lists.newArrayList();
        target = Lists.newArrayList();
 
        source.add(new Framework("Flexible Ajax", php, "PHP"));
        source.add(new Framework("ajaxCFC", cf,"CF"));
        source.add(new Framework("AJAXEngine", dotNet,"DOTNET"));
        source.add(new Framework("AjaxAC", php,"PHP"));
        source.add(new Framework("MonoRail", dotNet,"DOTNET"));
        source.add(new Framework("wddxAjax", cf,"CF"));
        source.add(new Framework("AJAX AGENT", php,"PHP"));
        source.add(new Framework("FastPage", dotNet,"DOTNET"));
        source.add(new Framework("JSMX", cf,"CF"));
        source.add(new Framework("PAJAJ", php,"PHP"));
        source.add(new Framework("Symfony", php,"PHP"));
        source.add(new Framework("PowerWEB", dotNet,"DOTNET"));
    }
}

*/