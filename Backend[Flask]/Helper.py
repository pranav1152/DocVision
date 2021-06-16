import cv2
import pytesseract
from matplotlib.pyplot import imshow
def adaptivethresholding(filename, ret):
    img = cv2.imread(filename)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    blur = cv2.bilateralFilter(img, 2, 10, 10) 
    thresh = cv2.adaptiveThreshold(blur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, 
                                      cv2.THRESH_BINARY, 11, 5) 
    if ret:
        return thresh
    else:
        cv2.imwrite(filename[:-4]+"_op.jpg", thresh);
    
def ocr(filename):
    img = cv2.imread(filename)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = cv2.bilateralFilter(img, 2, 10, 10) 
    img = cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, 
                                      cv2.THRESH_BINARY, 11, 5)
    #cv2.imshow("window",img)
    #cv2.waitKey(0)  
    
    #cv2.destroyAllWindows()  
    text = pytesseract.image_to_string(img, config = "--psm 6")
    return text

#print(ocr("ReovwExC3t.jpg"))


    