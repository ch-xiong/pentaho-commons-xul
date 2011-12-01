package org.pentaho.ui.xul.gwt.tags;

import com.google.gwt.core.client.GWT;
import org.pentaho.gwt.widgets.client.toolbar.ToolbarButton;
import org.pentaho.gwt.widgets.client.toolbar.ToolbarToggleButton;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulToolbarbutton;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.gwt.AbstractGwtXulComponent;
import org.pentaho.ui.xul.gwt.GwtXulHandler;
import org.pentaho.ui.xul.gwt.GwtXulParser;
import org.pentaho.ui.xul.stereotype.Bindable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

public class GwtToolbarbutton extends AbstractGwtXulComponent implements XulToolbarbutton {

  private ToolbarButton button;
  private String jscommand;

  private String dir, group, image, onclick, tooltip, disabledImage, type, downimage, downimagedisabled;

  private enum Property{
    ID, LABEL, DISABLED, ONCLICK, IMAGE, TOOLTIPTEXT, VISIBLE
  }

  public GwtToolbarbutton() {
    super("toolbarbutton");
  }

  public static void register() {
    GwtXulParser.registerHandler("toolbarbutton", new GwtXulHandler() {
      public Element newInstance() {
        return new GwtToolbarbutton();
      }
    });
  }

  @Override
  public void setAttribute(String name, String value) {
    super.setAttribute(name, value);
    try{
      Property prop = Property.valueOf(name.replace("pen:", "").toUpperCase());
      switch (prop) {

        case LABEL:
          setLabel(value);
          break;
        case DISABLED:
          setDisabled("true".equals(value));
          break;
        case ONCLICK:
          setOnclick(value);
          break;
        case IMAGE:
          setImage(value);
          break;
        case TOOLTIPTEXT:
          setTooltiptext(value);
          break;
        case VISIBLE:
          setVisible("true".equals(value));
          break;
      }
    } catch(IllegalArgumentException e){
      System.out.println("Could not find Property in Enum for: "+name+" in class"+getClass().getName());
    }
  }

  public void init(com.google.gwt.xml.client.Element srcEle, XulDomContainer container) {
    super.init(srcEle, container);
    setType(srcEle.getAttribute("type"));

    if (this.type != null && this.type.equals("toggle")) {
      button = new ToolbarToggleButton(new Image());
    } else {
      button = new ToolbarButton(new Image());
    }
    setManagedObject(button);

    setLabel(srcEle.getAttribute("label"));
    setOnclick(srcEle.getAttribute("onclick"));
    setJscommand(srcEle.getAttribute("js-command"));
    setImage(srcEle.getAttribute("image"));
    setTooltiptext(srcEle.getAttribute("tooltiptext"));
    setDisabled("true".equals(srcEle.getAttribute("disabled")));
    setDisabledImage(srcEle.getAttribute("disabledimage"));
    setDownimage(srcEle.getAttribute("downimage"));
    String vis = srcEle.getAttribute("pen:visible");
    if (vis != null && !vis.equals("")) {
      setVisible("true".equals(vis));
    }

  }

  @Override
  public Object getManagedObject() {

    // HACK: ToolbarButtons are not Widget descendants...

    Object m = super.getManagedObject();
    if (m == null)
      return m;

    if ((!StringUtils.isEmpty(getId()) && (m instanceof ToolbarButton))) {
      ((ToolbarButton) m).setId(this.getId());
    }
    return m;
  }

  public void doClick() {
    button.getCommand().execute();
  }

  @Bindable
  public void setLabel(String label) {
    button.setText(label);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.ui.xul.components.XulButton#setOnClick(java.lang.String)
   */
  public void setOnclick(final String method) {
    this.onclick = method;
    if (method != null) {
      button.setCommand(new Command() {
        public void execute() {
          try {
            GwtToolbarbutton.this.getXulDomContainer().invoke(method, new Object[] {});
          } catch (XulException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }

  public String getJscommand() {
    return jscommand;
  }

  public void setJscommand(String jscommand) {
    this.jscommand = jscommand;
    if (jscommand != null) {
      button.setCommand(new Command() {
        public void execute() {
          executeJS(GwtToolbarbutton.this.jscommand);
        }
      });
    }
  }

  private native void executeJS(String js)
  /*-{
    try{
      $wnd.eval(js);
    } catch (e){
      alert("Javascript Error: " + e.message+"\n\n"+js);
    }
  }-*/;

  @Bindable
  public String getLabel() {
    return button.getText();
  }

  @Bindable
  public boolean isDisabled() {
    return !button.isEnabled();
  }

  @Bindable
  public void setDisabled(boolean dis) {
    button.setEnabled(!dis);
  }

  public String getDir() {
    return dir;
  }

  public String getGroup() {
    return group;
  }

  @Bindable
  public String getImage() {
    return image;
  }

  public String getOnclick() {
    return onclick;
  }

  public String getType() {
    return type;
  }

  @Bindable
  public boolean isSelected() {
    return false;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public void setGroup(String group) {
    this.group = group;
    // TODO: implement button group
  }

  @Bindable
  public void setImage(String src) {
    this.image = src;
    if(button == null){
      return;
    }
    if (src != null && src.length() > 0) {
      Image i = new Image(GWT.getModuleBaseURL() + src);
      // WebDriver support.. give the image a direct id we can use as a hook
      if (!StringUtils.isEmpty(this.getId())) {
        i.getElement().setId(this.getId().concat("_img"));
      }
      button.setImage(i);
    }
  }

  @Bindable
  public void setDisabledImage(String src) {
    this.disabledImage = src;
    if (src != null && src.length() > 0 && button != null) {
      button.setDisabledImage(new Image(GWT.getModuleBaseURL() + disabledImage));
    }
  }

  public String getDisabledImage() {
    return disabledImage;
  }

  @Bindable
  public void setSelected(String selected) {

  }

  @Bindable
  public void setSelected(boolean selected) {
    if (button instanceof ToolbarToggleButton) {
      ((ToolbarToggleButton) button).setSelected(selected, true);
    } else if (button instanceof ToolbarButton) {
    }

  }

  @Bindable
  public void setSelected(boolean selected, boolean fireEvent) {
    if (button instanceof ToolbarToggleButton) {
      ((ToolbarToggleButton) button).setSelected(selected, fireEvent);
    } else if (button instanceof ToolbarButton) {
    }

  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  @Bindable
  public void setTooltiptext(String tooltip) {
    super.setTooltiptext(tooltip);
    if (button != null) {
      button.setToolTip(tooltip);
    }
  }

  public String getDownimage() {
    return this.downimage;
  }

  public String getDownimagedisabled() {
    return this.downimagedisabled;
  }

  public void setDownimage(String img) {
    this.downimage = img;
    if (img != null && img.length() > 0 && button != null) {
      button.setDownImage(new Image(GWT.getModuleBaseURL() + img));
    }
  }

  public void setDownimagedisabled(String img) {
    this.downimagedisabled = img;
    if (img != null && img.length() > 0 && button != null) {
      button.setDownImageDisabled(new Image(GWT.getModuleBaseURL() + img));
    }
  }

  @Override
  @Bindable
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    button.setVisible(visible);
  }

}
