package amidst.gui.seedsearcher;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ResultCellRenderer<T> implements ListCellRenderer<T> {
	
	private final Function<T, String> stringMapper;
	
	public ResultCellRenderer(Function<T, String> stringMapper) {
		this.stringMapper = stringMapper;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T result,
			int index, boolean selected, boolean hasFocus) {
		
		JLabel label = new JLabel(stringMapper.apply(result));
		
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray), 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
			));
		
		if(selected) {
			label.setOpaque(true);
			label.setBackground(new Color(160, 190, 255));
		}
		return label;
	}
}
