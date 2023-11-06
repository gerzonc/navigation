#import "NVBottomSheetView.h"

#import <UIKit/UIKit.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/UIView+React.h>


@interface NVBottomSheetController : UIViewController

@property (nonatomic, copy) void (^boundsDidChangeBlock)(CGRect newBounds);

@end

@implementation NVBottomSheetController
{
    CGRect _lastViewFrame;
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (self.boundsDidChangeBlock && !CGRectEqualToRect(_lastViewFrame, self.view.frame)) {
        self.boundsDidChangeBlock(self.view.bounds);
        _lastViewFrame = self.view.frame;
    }
}

@end

@implementation NVBottomSheetView
{
    __weak RCTBridge *_bridge;
    NVBottomSheetController *_bottomSheetController;
    UIView *_reactSubview;
    CGSize _oldSize;
    NSInteger _nativeEventCount;
}

- (id)initWithBridge:(RCTBridge *)bridge
{
    if (self = [super init]) {
        _bridge = bridge;
        _bottomSheetController = [[NVBottomSheetController alloc] init];
        if (@available(iOS 15.0, *)) {
            _bottomSheetController.sheetPresentationController.delegate = self;
        }
        UIView *containerView = [UIView new];
        containerView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
        _bottomSheetController.view = containerView;
        CADisplayLink *displayLink = [CADisplayLink displayLinkWithTarget:self selector:@selector(updateView)];
        [displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        __weak typeof(self) weakSelf = self;
        _bottomSheetController.boundsDidChangeBlock = ^(CGRect newBounds) {
            [weakSelf notifyForBoundsChange:newBounds];
        };
    }
    return self;
}

- (void)setDetent:(NSString *)detent
{
    if (@available(iOS 15.0, *)) {
        [_bottomSheetController.sheetPresentationController animateChanges:^{
            _bottomSheetController.sheetPresentationController.selectedDetentIdentifier = [detent isEqual: @"collapsed"] ? UISheetPresentationControllerDetentIdentifierMedium : UISheetPresentationControllerDetentIdentifierLarge;
        }];
    }
}

- (void)notifyForBoundsChange:(CGRect)newBounds
{
    if (_reactSubview) {
        CAAnimation *dropping = [_bottomSheetController.view.layer animationForKey:@"bounds.size"];
        if (!dropping) {
            [_bridge.uiManager setSize:newBounds.size forView:_reactSubview];
        }
    }
}

- (void)updateView {
    CGSize newSize = [[_bottomSheetController.view.layer.presentationLayer valueForKeyPath:@"frame.size"] CGSizeValue];
    if (!CGSizeEqualToSize(_oldSize, newSize)) {
        _oldSize = newSize;
        [_bridge.uiManager setSize:newSize forView:_reactSubview];
    }
}


- (void)insertReactSubview:(UIView *)subview atIndex:(NSInteger)atIndex
{
    [super insertReactSubview:subview atIndex:atIndex];
    [_bottomSheetController.view insertSubview:subview atIndex:0];
    _reactSubview = subview;
}

- (void)removeReactSubview:(UIView *)subview
{
    [super removeReactSubview:subview];
    _reactSubview = nil;
}

- (void)didUpdateReactSubviews
{
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    if (@available(iOS 15.0, *)) {
        UISheetPresentationController *sheet = _bottomSheetController.sheetPresentationController;
        [sheet setDetents:@[UISheetPresentationControllerDetent.mediumDetent, UISheetPresentationControllerDetent.largeDetent]];
        [[self reactViewController] presentViewController:_bottomSheetController animated:true completion:nil];
    }
}

- (void)sheetPresentationControllerDidChangeSelectedDetentIdentifier:(UISheetPresentationController *)sheetPresentationController
API_AVAILABLE(ios(15.0)){
    _nativeEventCount++;
    if (!!self.onDetentChanged) {
        self.onDetentChanged(@{
            @"detent": sheetPresentationController.selectedDetentIdentifier == UISheetPresentationControllerDetentIdentifierMedium ? @"collapsed" : @"expanded",
            @"eventCount": @(_nativeEventCount),
        });
    }

}

@end

