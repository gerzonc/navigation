#import "NVBarButtonView.h"

#import <UIKit/UIKit.h>
#import <React/UIView+React.h>

@implementation NVBarButtonView

- (id)init
{
    if (self = [super init]) {
        self.button = [[UIBarButtonItem alloc] init];
        self.button.style = UIBarButtonItemStylePlain;
        self.button.target = self;
        self.button.action = @selector(buttonPressed);
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    UIView *buttonView = ((UIView *) [self.button valueForKey:@"view"]);
    UIView *rightBarView = buttonView.superview;
    UIView *labelView = buttonView.subviews.count > 0 ? buttonView.subviews[0] : buttonView;
    CGRect labelFrameInBar = [buttonView convertRect:labelView.frame toView:rightBarView];
    self.frame = [rightBarView convertRect:labelFrameInBar toView:nil];
}

- (void)setTitle:(NSString *)title
{
    self.button.title = title;
}

- (void)setImage:(UIImage *)image
{
    self.button.image = image;
}

- (void)setSystemItem:(UIBarButtonSystemItem)systemItem
{
    self.button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:systemItem target:self action:@selector(buttonPressed)];
}

-(void)buttonPressed
{
    if (!!self.onPress) {
        self.onPress(nil);
    }
}

@end
