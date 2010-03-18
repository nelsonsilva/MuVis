package muvis.view.filters;

import net.bouthier.treemapSwing.TMComputeSize;
import net.bouthier.treemapSwing.TMView;
import net.bouthier.treemapSwing.TreeMap;

public class TMViewFactory{
    public TMView createView(MuVisFilterNode filterNode, TMComputeSize durationFilterSize, MuVisFilterDraw durationFilterDraw,TMAlgorithmAscOrderFilter order) {
        TreeMap filterTreeMap=new TreeMap(filterNode);
        TMView view=filterTreeMap.getView(durationFilterSize, durationFilterDraw);
        MuVisFilterAction durationAction = new MuVisFilterAction(view);
        view.setAction(durationAction);
        view.addAlgorithm(order, order.getName());
        view.setAlgorithm(order.getName());
        return view;
    }
}
