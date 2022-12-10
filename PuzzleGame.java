import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Image;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;


class FancyButton extends JButton{
    
    public FancyButton()
    {
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            }
            @Override    
            public void mouseExited(MouseEvent e) 
            {
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
    });     }
}

public class PuzzleGame extends JFrame{
    JPanel panel;
    final int NUMROW = 4, NUMCOL = 4;
    List<FancyButton> allButtons = new ArrayList<FancyButton>();
    List<FancyButton> solution = new ArrayList<FancyButton>();
    final int WIDTH = 600;
    int HEIGHT = 600; 
    BufferedImage ogImage;//original image
    BufferedImage scaledImage;//scale to window size
   

   

    public PuzzleGame()
    {
        super("CSC295 PuzzleGame");
        panel = new JPanel();
        panel.setLayout(new GridLayout(NUMROW,NUMCOL));
        add(panel);
        panel.setOpaque(true);
        
    
        
        //creat buttons and add them to the panel
        try{
            ogImage =loadImage();
            //get scaledImage
            int ogWidth = ogImage.getWidth();
            int ogHeight = ogImage.getHeight();
            //reset from ogWidth/ogHeight _>>> WIDTH/ newHight
            HEIGHT= (int)((double)ogWidth/ogHeight*WIDTH);
    
            //resized image
            scaledImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
            var g = scaledImage.createGraphics();
            g.drawImage(ogImage, 0,0,ogWidth, ogHeight, null);
            g.dispose();
            }
            catch(IOException e)
            {
                JOptionPane.showMessageDialog(this, "ERROR: "+e.getMessage(),"No image found,", JOptionPane.ERROR_MESSAGE);
            }for (int n=0; n<(NUMROW*NUMCOL); n++)



        {
            int row = n / NUMCOL;
            int col = n % NUMCOL;

            FancyButton btn = new FancyButton();//creat new button
            panel.add(btn);
            allButtons.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            btn.setOpaque(true);
            btn.addActionListener(e->MyClickEventHandler(e));

            //get the right slice from scaled image into the button
            Image imageSlice = createImage(new FilteredImageSource(scaledImage.getSource(),
            new CropImageFilter(col*WIDTH/NUMCOL, row*HEIGHT/NUMROW, WIDTH/NUMCOL, HEIGHT/NUMROW)
            ));

            if(n == NUMCOL*NUMROW-1)
            {
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
            }
            else{
                btn.setIcon(new ImageIcon(imageSlice));
            }
           
        }

        //save the solution
       solution = List.copyOf((allButtons));
       Collections.shuffle(allButtons);
       for(var btn : allButtons)
       {
        panel.add(btn);
       }




        setSize(WIDTH,HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
    }
    
    public void MyClickEventHandler(ActionEvent e)
    {
            //what button was clicked
        FancyButton btn = (FancyButton) e.getSource();
        int j = allButtons.indexOf(btn);
        int btnRow = j/NUMCOL, btnCol = j%NUMCOL;

            //whhere is the empty button
            int i= -1;
            for (i=0; i<allButtons.size(); i++)
            {
                if(allButtons.get(i).getIcon()==null)
                {
                    break;
                }
            }
            int emptyRow = i/NUMCOL, emptyCol =i%NUMCOL;

            //if adjesent switch
            if((emptyRow == btnRow && Math.abs(emptyCol - btnCol)==1) || (emptyCol == btnCol && Math.abs(emptyRow - btnRow)==1))
            {
                Collections.swap(allButtons, i, j);
                //update the grid
                panel.removeAll();
                for(var btn_ : allButtons)
                {
                    panel.add(btn_);
                }
                panel.validate();
            }
            if(allButtons.equals(solution))
            {
                JOptionPane.showMessageDialog(null, "You Win!");
            }
    }

    BufferedImage loadImage() throws IOException
    {
        return ImageIO.read(new File("husky.png"));
    }
}




