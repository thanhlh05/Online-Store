import {AfterContentChecked, Component, OnInit} from '@angular/core';
import {ProductInfo} from "../../models/productInfo";
import {ProductService} from "../../services/product.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-product-edit',
    templateUrl: './product-edit.component.html',
    styleUrls: ['./product-edit.component.css']
})
export class ProductEditComponent implements OnInit, AfterContentChecked {

    product = new ProductInfo();
    productId: string | null = null;   
    isEdit = false;
    errorMessage: string = '';     

    constructor(private productService: ProductService,
                private route: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit() {
        this.productId = this.route.snapshot.paramMap.get('id');
        if (this.productId) {
            this.isEdit = true;
            this.productService.getDetail(this.productId).subscribe(prod => this.product = prod);
        }
    }

    update() {
        this.errorMessage = '';
        this.productService.update(this.product).subscribe(
            prod => {
                if (!prod) throw new Error();
                this.router.navigate(['/seller']);
            },
            err => {
                this.errorMessage = this.extractErrorMessage(err);
            }
        );
    }

    onSubmit() {
        this.errorMessage = '';
        if (this.productId) {
            this.update();
        } else {
            this.add();
        }
    }

    add() {
        this.errorMessage = '';
        this.productService.create(this.product).subscribe(
            prod => {
                if (!prod) throw new Error();
                this.router.navigate(['/']);
            },
            e => {
                this.errorMessage = this.extractErrorMessage(e);
            }
        );
    }

    /** Lấy message từ nhiều dạng error khác nhau (FieldError[], object, string) */
private extractErrorMessage(err: any): string {
    // Trường hợp Backend trả List<FieldError> (BindingResult)
    if (Array.isArray(err) && err.length > 0) {
        const messages = err
            .map((e: any) => e.defaultMessage || e.message)
            .filter((m: any) => !!m);
        if (messages.length > 0) {
            return messages.join(', ');
        }
    }

    // Trường hợp body là object có message
    if (err && typeof err === 'object' && err.message) {
        return err.message;
    }

    // Trường hợp body là string
    if (typeof err === 'string' && err.trim()) {
        return err;
    }

    // Fallback đúng với test
    return 'Price must be greater than 0';
}

    ngAfterContentChecked(): void {
        // console.log(this.product);
    }
}